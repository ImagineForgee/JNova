package jnova.tcp;

import jnova.core.Server;
import jnova.core.events.EventBuilder;
import jnova.core.events.EventBus;
import jnova.core.events.EventType;
import jnova.core.events.impl.ServerStartEvent;
import jnova.tcp.framing.FramingStrategy;
import jnova.tcp.framing.LineFraming;
import jnova.tcp.request.TcpBinaryRequest;
import jnova.tcp.handler.TcpRequestHandler;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class TcpServer extends Server {
    private ServerSocket serverSocket;
    private final FramingStrategy framingStrategy;
    private final ExecutorService threadPool;
    private final Map<String, TcpSession> sessionMap = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    private final Duration idleTimeout = Duration.ofSeconds(30);

    private final TcpRequestHandler handler;

    private final EventBus eventBus = getEventBus();

    public TcpServer(TcpRequestHandler handler) {
        this(handler, Executors.newCachedThreadPool(), new LineFraming());
    }

    public TcpServer(TcpRequestHandler handler, ExecutorService pool, FramingStrategy framingStrategy) {
        this.handler = handler;
        this.threadPool = pool;
        this.framingStrategy = framingStrategy;
    }

    @Override
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("JNova TCP Server listening on port " + port);
        ServerStartEvent startEvent = EventBuilder.ofType(EventType.SERVER_START, ServerStartEvent::new)
                .fromSource(this)
                .with("port", port)
                .with("address", serverSocket.getInetAddress())
                .build();
        eventBus.emit(startEvent);
        while (running) {
            Socket client = serverSocket.accept();
            threadPool.submit(() -> handleClient(client));
        }
    }

    private void handleClient(Socket socket) {
        String sessionId = UUID.randomUUID().toString();
        System.out.println("New connection [" + sessionId + "] from " + socket.getInetAddress());

        CountDownLatch latch = new CountDownLatch(1);

        try (TcpSession session = new TcpSession(socket, sessionId)) {
            sessionMap.put(sessionId, session);
            InputStream in = socket.getInputStream();

            framingStrategy.readMessages(in)
                    .timeout(idleTimeout)
                    .flatMap(messageBytes -> {
                        TcpBinaryRequest request = new TcpBinaryRequest(messageBytes, session);
                        return handler.handle(request)
                                .flatMap(response -> session.send(response.getBytes()))
                                .onErrorResume(e -> {
                                    System.err.println("[" + sessionId + "] Handler error: " + e.getMessage());
                                    return Mono.empty();
                                });
                    })
                    .doOnError(TimeoutException.class, e -> {
                        System.out.println("[" + sessionId + "] Idle timeout reached. Closing session.");
                    })
                    .doOnError(e -> {
                        if (!(e instanceof TimeoutException)) {
                            System.err.println("[" + sessionId + "] Unexpected error: " + e.getMessage());
                        }
                    })
                    .doFinally(signal -> {
                        sessionMap.remove(sessionId);
                        System.out.println("[" + sessionId + "] Session closing due to: " + signal);
                        try {
                            session.close();
                        } catch (IOException e) {
                            System.err.println("[" + sessionId + "] Error during session close: " + e.getMessage());
                        }
                        latch.countDown();
                    })
                    .subscribe();

            latch.await();
        } catch (IOException e) {
            System.err.println("[" + sessionId + "] Connection error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[" + sessionId + "] Thread interrupted: " + e.getMessage());
        }
    }




    @Override
    public void stop() throws IOException {
        running = false;
        if (serverSocket != null) serverSocket.close();
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
    }

}
