package jnova.tcp;

import jnova.core.session.Session;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TcpSession implements Session {
    private final Socket socket;
    private final String sessionId;
    private final OutputStream output;
    private final AtomicLong lastKeepAlive = new AtomicLong(System.currentTimeMillis());
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public TcpSession(Socket socket, String sessionId) throws IOException {
        this.socket = socket;
        this.sessionId = sessionId;
        this.output = socket.getOutputStream();
    }

    @Override
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

    @Override
    public void keepAlive() {
        lastKeepAlive.set(System.currentTimeMillis());
    }

    @Override
    public long getLastKeepAlive() {
        return lastKeepAlive.get();
    }

    @Override
    public boolean isAlive() {
        return !socket.isClosed();
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public Mono<Void> closeAsync() {
        return Mono.fromRunnable(() -> {
            try {
                close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
