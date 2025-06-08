package jnova.tcp;

import reactor.core.publisher.Mono;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class TcpSession implements Closeable {
    private final Socket socket;
    private final String sessionId;
    private final OutputStream output;
    private final AtomicLong lastKeepAlive = new AtomicLong(System.currentTimeMillis());

    public TcpSession(Socket socket, String sessionId) throws IOException {
        this.socket = socket;
        this.sessionId = sessionId;
        this.output = socket.getOutputStream();
    }

    public Mono<Void> send(byte[] data) {
        return Mono.fromRunnable(() -> {
            try {
                output.write(data);
                output.flush();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public void keepAlive() {
        lastKeepAlive.set(System.currentTimeMillis());
    }

    public long getLastKeepAlive() {
        return lastKeepAlive.get();
    }

    public boolean isAlive() {
        return !socket.isClosed();
    }

    public String getId() {
        return sessionId;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
