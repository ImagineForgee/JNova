package jnova.tcp;

import com.google.gson.Gson;
import jnova.core.session.Session;
import jnova.tcp.framing.FramingStrategy;
import jnova.tcp.protocol.TcpMessage;
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
    private Map<String, TcpSession> sessions = new ConcurrentHashMap<>();
    private final Object writeLock = new Object();

    public TcpSession(Socket socket, String sessionId, FramingStrategy framingStrategy) throws IOException {
        this.socket = socket;
        this.sessionId = sessionId;
        this.framingStrategy = framingStrategy;
    }

    public Mono<Void> send(byte[] message) {
        return Mono.fromRunnable(() -> {
            synchronized (writeLock) {
                try {
                    framingStrategy.writeMessage(socket.getOutputStream(), message);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });
    }

    public void broadcast(byte[] message) {
        for (TcpSession s : sessions.values()) {
            s.send(message).subscribe();
        }
    }

    public void setSessions(Map<String, TcpSession> sessions) {
        this.sessions = sessions;
    }

    @Override
    public void touch() {
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
