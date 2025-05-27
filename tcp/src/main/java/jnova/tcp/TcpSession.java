package jnova.tcp;

import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpSession implements AutoCloseable {
    private final Socket socket;
    private final String sessionId;
    private final BufferedWriter writer;
    private volatile boolean closed = false;

    private static final ExecutorService sendExecutor = Executors.newCachedThreadPool();

    public TcpSession(Socket socket, String sessionId) throws IOException {
        this.socket = socket;
        this.sessionId = sessionId;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public Mono<Void> send(byte[] data) {
        return Mono.create(sink -> {
            if (closed) {
                sink.error(new IllegalStateException("Session already closed"));
                return;
            }
            sendExecutor.submit(() -> {
                try {
                    socket.getOutputStream().write(data);
                    socket.getOutputStream().flush();
                    sink.success();
                } catch (IOException e) {
                    sink.error(e);
                }
            });
        });
    }

    @Override
    public void close() throws IOException {
        closed = true;
        writer.close();
        socket.close();
    }
}
