package jnova.core.session;

import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.Map;

/**
 * Represents a communication session between a client and a server.
 *
 * <p>Provides methods for sending and receiving data, managing session state,
 * and accessing session metadata. Implements {@link Closeable} for resource management.</p>
 */
public interface Session extends Closeable {
        /**
     * Retrieves the ID.
     *
     * @return The ID as a String.
     */
    String getId();
        /**
     * Sends the provided data.
     *
     * @param data The byte array containing the data to send.
     * @return A {@link Mono<Void>} representing the completion of the send operation.
     */
    Mono<Void> send(byte[] data);
        /**
     * Simulates a touch event.
     */
    void touch();
        /**
     * Returns the timestamp of the last keep-alive signal received.
     *
     * @return The timestamp (in milliseconds) of the last keep-alive signal.
     */
    long getLastKeepAlive();
        /**
     * Checks if the entity is currently alive.
     *
     * @return true if the entity is alive, false otherwise.
     */
    boolean isAlive();

    /**
     * Gets the remote address (IP and port) of the client.
     */
        /**
     * Returns the remote address that this socket is connected to, or {@code null} if the socket is not connected.
     *
     * @return the remote address to which this socket is connected, or {@code null} if it is not connected.
     */
    SocketAddress getRemoteAddress();

    /**
     * A map for storing custom session-scoped metadata (e.g., username, auth tokens).
     */
        /**
     * Returns a map of attributes associated with this object.
     *
     * The map contains key-value pairs representing the attributes and their corresponding values.
     *
     * @return A map containing the attributes of this object.
     */
    Map<String, Object> attributes();

    /**
     * Async version of close.
     */
        /**
     * Asynchronously closes the resource.
     *
     * @return A {@link Mono} that completes when the resource is closed.
     */
    Mono<Void> closeAsync();
}

