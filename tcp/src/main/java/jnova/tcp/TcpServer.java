package jnova.tcp;

import jnova.core.Server;
import jnova.tcp.framing.FramingStrategy;
import jnova.tcp.framing.LineFraming;
import jnova.tcp.request.TcpBinaryRequest;
import jnova.tcp.request.TcpRequest;
import jnova.tcp.request.TcpRequestHandler;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer implements Server {
    private ServerSocket serverSocket;
    private final FramingStrategy framingStrategy;
    private final ExecutorService threadPool;
    private final Map<String, TcpSession> sessionMap = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    private final TcpRequestHandler handler;

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
            framingStrategy.readMessages(in, messageBytes -> {
                TcpBinaryRequest request = new TcpBinaryRequest(messageBytes, session);
                handler.handle(request)
                        .flatMap(response -> session.send(response.getBytes()))
                        .subscribe();
            }, error -> {
                System.err.println("[" + sessionId + "] Error reading: " + error.getMessage());
                latch.countDown();
            });
            latch.await();
        } catch (IOException e) {
            System.err.println("[" + sessionId + "] Connection error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[" + sessionId + "] Thread interrupted: " + e.getMessage());
        } finally {
            sessionMap.remove(sessionId);
            System.out.println("[" + sessionId + "] Session closing.");
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("[" + sessionId + "] Error during socket close: " + e.getMessage());
            }
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
