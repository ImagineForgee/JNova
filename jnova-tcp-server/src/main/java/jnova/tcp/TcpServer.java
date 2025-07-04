package jnova.tcp;

import jnova.core.Server;
import jnova.core.events.EventBuilder;
import jnova.core.events.EventBus;
import jnova.core.events.EventType;
import jnova.core.events.impl.ServerErrorEvent;
import jnova.core.events.impl.ServerStartEvent;
import jnova.core.events.impl.ServerStopEvent;
import jnova.tcp.dispatching.TcpMiddleware;
import jnova.tcp.events.TcpMessageReceivedEvent;
import jnova.tcp.events.TcpSessionCloseEvent;
import jnova.tcp.events.TcpSessionErrorEvent;
import jnova.tcp.events.TcpSessionOpenEvent;
import jnova.tcp.framing.FramingStrategy;
import jnova.tcp.framing.LineFraming;
import jnova.tcp.handler.TcpRequestHandler;
import jnova.tcp.request.TcpBinaryRequest;
import jnova.tcp.util.KeepAliveMonitor;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.SignalType;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * A TCP server implementation that handles client connections and processes requests.
 * <p>
 * This class extends the {@link Server} abstract class and provides functionality for
 * accepting client connections, managing sessions, handling requests using a provided
 * {@link TcpRequestHandler}, and applying middleware for request processing. It uses
 * a thread pool to handle multiple concurrent client connections and supports configurable
 * framing strategies for message parsing.
 * <p>
 * The server also incorporates a keep-alive mechanism to monitor and close idle sessions,
 * and utilizes an event bus for publishing various events related to server lifecycle and
 * session activity.
 */
public class TcpServer extends Server {
    private final FramingStrategy framingStrategy;
    private final ExecutorService threadPool;
    private final Map<String, TcpSession> sessionMap = new ConcurrentHashMap<>();
    private final Duration idleTimeout = Duration.ofSeconds(30);
    private final TcpRequestHandler handler;
    private final List<TcpMiddleware> middleware;
    private final EventBus eventBus = getEventBus();
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private KeepAliveMonitor keepAliveMonitor;

    /**
     * Constructs a TcpServer with the given request handler, a cached thread pool, line framing, and no initial filters.
     *
     * @param handler The request handler to use for processing incoming TCP requests.
     */
    public TcpServer(TcpRequestHandler handler) {
        this(handler, Executors.newCachedThreadPool(), new LineFraming(), List.of());
    }

    /**
     * Constructs a new TcpServer with the specified request handler, executor pool, and framing strategy.
     *
     * @param handler         The request handler to use for processing incoming TCP requests.
     * @param pool            The executor service to use for managing threads that handle requests.
     * @param framingStrategy The framing strategy to use for delineating messages.
     */
    public TcpServer(TcpRequestHandler handler, ExecutorService pool, FramingStrategy framingStrategy) {
        this(handler, pool, framingStrategy, List.of());
    }

    /**
     * Constructs a new TcpServer.
     *
     * @param handler         The request handler for processing incoming TCP requests.
     * @param pool            The executor service for managing threads.
     * @param framingStrategy The strategy for framing TCP messages.
     * @param middleware      A list of middleware to be executed on each request. Can be null or empty.
     */
    public TcpServer(TcpRequestHandler handler, ExecutorService pool, FramingStrategy framingStrategy, List<TcpMiddleware> middleware) {
        this.handler = handler;
        this.threadPool = pool;
        this.framingStrategy = framingStrategy;
        this.middleware = middleware != null ? middleware : List.of();
    }

    /**
     * Starts the TCP server, listening for incoming client connections on the specified port.
     *
     * <p>This method initializes the server socket, starts a keep-alive monitor,
     * and enters a loop to accept and handle client connections.  It also emits
     * events for server startup and errors.</p>
     *
     * @param port The port number to listen on.
     * @throws IOException If an I/O error occurs while starting the server.
     */
    @Override
    public void start(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("JNova TCP Server listening on port " + port);

            keepAliveMonitor = new KeepAliveMonitor(sessionMap, idleTimeout);
            keepAliveMonitor.start();

            eventBus.emit(EventBuilder.ofType(EventType.SERVER_START, ServerStartEvent::new)
                    .fromSource(this)
                    .with("port", port)
                    .with("address", serverSocket.getInetAddress())
                    .build());

            while (running) {
                Socket client = serverSocket.accept();
                threadPool.submit(() -> handleClient(client));
            }
        } catch (IOException e) {
            eventBus.emit(EventBuilder.ofType(EventType.SERVER_ERROR, ServerErrorEvent::new)
                    .fromSource(this)
                    .with("port", port)
                    .with("address", serverSocket != null ? serverSocket.getInetAddress() : null)
                    .with("error", e)
                    .build());
            throw e;
        }
    }

    /**
     * Stops the TCP server, closing resources and notifying clients.
     * <p>
     * This method performs the following actions:
     * 1. Sets the `running` flag to false, indicating the server is no longer active.
     * 2. Closes the `serverSocket` to prevent new connections.
     * 3. Stops the `keepAliveMonitor` to halt keep-alive checks.
     * 4. Iterates through active sessions, notifying each client about the server shutdown and closing the session.
     * 5. Shuts down the `threadPool` to terminate active threads.
     * 6. Emits a `SERVER_STOP` event to the event bus.
     *
     * @throws IOException if an error occurs while closing the server socket or client sessions.
     */
    @Override
    public void stop() throws IOException {
        running = false;
        if (serverSocket != null) {
            serverSocket.close();
        }

        if (keepAliveMonitor != null) {
            keepAliveMonitor.stop();
        }

        System.out.println("Notifying clients about shutdown...");
        sessionMap.forEach((id, session) -> {
            session.send("Server is shutting down. Goodbye!".getBytes())
                    .doOnTerminate(() -> {
                        try {
                            session.close();
                        } catch (IOException e) {
                            System.err.println("Error closing session " + id + ": " + e.getMessage());
                        }
                    })
                    .block();
        });

        threadPool.shutdownNow();
        System.out.println("JNova TCP Server stopped.");
        eventBus.emit(EventBuilder.ofType(EventType.SERVER_STOP, ServerStopEvent::new)
                .fromSource(this)
                .build());
    }

    /**
     * Handles a client connection on the given socket.
     *
     * <p>This method manages the lifecycle of a TCP session, including establishing the session,
     * handling incoming messages, processing requests, sending responses, and closing the session.
     * It also incorporates middleware for handling connection events, exceptions, timeouts, and protocol errors.
     *
     * @param socket The socket representing the client connection.
     */
    private void handleClient(Socket socket) {
        String sessionId = UUID.randomUUID().toString();
        System.out.println("New connection [" + sessionId + "] from " + socket.getInetAddress());
        CountDownLatch latch = new CountDownLatch(1);

        try (TcpSession session = new TcpSession(socket, sessionId, framingStrategy)) {
            sessionMap.put(sessionId, session);
            session.setSessions(sessionMap);

            middleware.forEach(mw -> mw.onConnect(session));

            eventBus.emit(EventBuilder.ofType(EventType.TCP_SESSION_OPEN, TcpSessionOpenEvent::new)
                    .fromSource(session)
                    .with("sessionId", sessionId)
                    .with("remoteAddress", socket.getInetAddress())
                    .build());

            InputStream in = socket.getInputStream();

            framingStrategy.readMessages(in)
                    .timeout(idleTimeout)
                    .onBackpressureBuffer(
                            1,
                            dropped -> System.err.println("[" + sessionId + "] Dropped message due to backpressure"),
                            BufferOverflowStrategy.DROP_OLDEST
                    )
                    .subscribeWith(new BaseSubscriber<byte[]>() {
                        @Override
                        protected void hookOnSubscribe(Subscription subscription) {
                            request(1);
                        }

                        @Override
                        protected void hookOnNext(byte[] messageBytes) {
                            eventBus.emit(EventBuilder.ofType(EventType.TCP_MESSAGE_RECEIVED, TcpMessageReceivedEvent::new)
                                    .fromSource(session)
                                    .with("sessionId", sessionId)
                                    .with("message", messageBytes)
                                    .build());

                            TcpBinaryRequest request = new TcpBinaryRequest(messageBytes, session);

                            handler.handle(request)
                                    .flatMap(response -> session.send(response.getBytes()))
                                    .delayElement(Duration.ofSeconds(10))
                                    .doOnError(e -> {
                                        middleware.forEach(mw -> mw.onException(e, null, session));
                                        System.err.println("[" + sessionId + "] Handler error: " + e.getMessage());
                                    })
                                    .doOnTerminate(() -> request(1))
                                    .subscribe();
                        }

                        @Override
                        protected void hookOnError(Throwable e) {
                            if (e instanceof TimeoutException) {
                                middleware.forEach(mw -> mw.onTimeout(session));
                                System.out.println("[" + sessionId + "] Idle timeout reached. Closing session.");
                            } else {
                                middleware.forEach(mw -> mw.onProtocolError(e, session));
                                System.err.println("[" + sessionId + "] Unexpected error: " + e.getMessage());
                                eventBus.emit(EventBuilder.ofType(EventType.TCP_SESSION_ERROR, TcpSessionErrorEvent::new)
                                        .fromSource(session)
                                        .with("sessionId", sessionId)
                                        .with("error", e)
                                        .build());
                            }
                        }

                        @Override
                        protected void hookFinally(SignalType signalType) {
                            sessionMap.remove(sessionId);
                            System.out.println("[" + sessionId + "] Session closing due to: " + signalType);

                            middleware.forEach(mw -> mw.onDisconnect(session));

                            eventBus.emit(EventBuilder.ofType(EventType.TCP_SESSION_CLOSE, TcpSessionCloseEvent::new)
                                    .fromSource(session)
                                    .with("sessionId", sessionId)
                                    .with("reason", signalType.name())
                                    .build());

                            try {
                                session.close();
                            } catch (IOException e) {
                                System.err.println("[" + sessionId + "] Error during session close: " + e.getMessage());
                            }

                            latch.countDown();
                        }
                    });

            latch.await();
        } catch (IOException e) {
            System.err.println("[" + sessionId + "] Connection error: " + e.getMessage());
            eventBus.emit(EventBuilder.ofType(EventType.TCP_SESSION_ERROR, TcpSessionErrorEvent::new)
                    .fromSource(this)
                    .with("sessionId", sessionId)
                    .with("error", e)
                    .build());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[" + sessionId + "] Thread interrupted: " + e.getMessage());
        }
    }
}
