package jnova.tcp;

import jnova.core.session.Session;
import jnova.tcp.framing.FramingStrategy;
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
    private final FramingStrategy framingStrategy;
    private final AtomicLong lastKeepAlive = new AtomicLong(System.currentTimeMillis());
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public TcpSession(Socket socket, String sessionId, FramingStrategy framingStrategy) throws IOException {
        this.socket = socket;
        this.sessionId = sessionId;
        this.framingStrategy = framingStrategy;
    }

    public Mono<Void> send(byte[] message) {
        return Mono.fromRunnable(() -> {
            try {
                framingStrategy.writeMessage(socket.getOutputStream(), message);
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
