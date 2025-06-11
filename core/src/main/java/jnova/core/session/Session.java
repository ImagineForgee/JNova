package jnova.core.session;

import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.Map;

public interface Session extends Closeable {
    String getId();
    Mono<Void> send(byte[] data);
    void touch();
    long getLastKeepAlive();
    boolean isAlive();

    /**
     * Gets the remote address (IP and port) of the client.
     */
    SocketAddress getRemoteAddress();

    /**
     * A map for storing custom session-scoped metadata (e.g., username, auth tokens).
     */
    Map<String, Object> attributes();

    /**
     * Async version of close.
     */
    Mono<Void> closeAsync();
}

