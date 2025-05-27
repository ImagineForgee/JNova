package jnova.tcp;

import jnova.core.Server;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer implements Server {
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final Map<String, TcpSession> sessionMap = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    private final TcpRequestHandler handler;

    public TcpServer(TcpRequestHandler handler) {
        this(handler, Executors.newCachedThreadPool());
    }

    public TcpServer(TcpRequestHandler handler, ExecutorService executorService) {
        this.handler = handler;
        this.threadPool = executorService;
    }

    @Override
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("JNova TCP Server listening on port " + port);

        while (running) {
            Socket client = serverSocket.accept();
            threadPool.submit(() -> handleClient(client));
        }
    }

    private void handleClient(Socket socket) {
        String sessionId = UUID.randomUUID().toString();
        System.out.println("New connection [" + sessionId + "] from " + socket.getInetAddress());

        CountDownLatch latch = new CountDownLatch(1);

        try (TcpSession session = new TcpSession(socket, sessionId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
        ) {
            sessionMap.put(sessionId, session);
            Flux<String> linesFlux = Flux.<String>create(emitter -> {
                        try {
                            String line;
                            while ((line = reader.readLine()) != null && !emitter.isCancelled()) {
                                emitter.next(line);
                            }
                            emitter.complete();
                        } catch (IOException e) {
                            emitter.error(e);
                        }
                    })
                    .doOnCancel(() -> System.out.println("[" + sessionId + "] Input reading cancelled"))
                    .doOnComplete(() -> {
                        System.out.println("[" + sessionId + "] Input reading completed");
                        latch.countDown();
                    })
                    .doOnError(e -> {
                        System.err.println("[" + sessionId + "] Input reading failed: " + e.getMessage());
                        latch.countDown();
                    });

            linesFlux
                    .flatMapSequential(line -> {
                        System.out.println("[" + sessionId + "] Received: " + line);
                        TcpRequest request = new TcpRequest(line, session);

                        return handler.handle(request)
                                .flatMap(response -> {
                                    if (response != null && response.getBody() != null) {
                                        return session.send(response.getBody());
                                    }
                                    return Mono.empty();
                                })
                                .onErrorResume(e -> {
                                    System.err.println("[" + sessionId + "] Handler error: " + e.getMessage());
                                    return Mono.empty();
                                });
                    })
                    .doFinally(signal -> {
                        sessionMap.remove(sessionId);
                        System.out.println("[" + sessionId + "] Session closing due to: " + signal);
                        try {
                            session.close();
                        } catch (IOException e) {
                            System.err.println("[" + sessionId + "] Error during session close: " + e.getMessage());
                        }
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
            session.send("Server is shutting down. Goodbye!")
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
