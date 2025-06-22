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

/**
 * Represents a TCP session, managing communication over a socket.
 *
 * <p>This class handles sending and receiving data, managing session attributes,
 * and tracking session lifecycle events like keep-alive and closure. It uses a
 * {@link FramingStrategy} to handle message framing over the TCP stream.
 */
public class TcpSession implements Session {
    private final Socket socket;
    private final String sessionId;
    private final FramingStrategy framingStrategy;
    private final AtomicLong lastKeepAlive = new AtomicLong(System.currentTimeMillis());
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private Map<String, TcpSession> sessions = new ConcurrentHashMap<>();
    private final Object writeLock = new Object();

        /**
     * Constructs a TcpSession with the given socket, session ID, and framing strategy.
     *
     * @param socket The socket associated with this session.
     * @param sessionId The unique identifier for this session.
     * @param framingStrategy The strategy used for framing messages.
     * @throws IOException If an I/O error occurs during socket initialization.
     */
    public TcpSession(Socket socket, String sessionId, FramingStrategy framingStrategy) throws IOException {
        this.socket = socket;
        this.sessionId = sessionId;
        this.framingStrategy = framingStrategy;
    }

        /**
     * Sends a message through the socket.
     *
     * The message is written to the output stream of the socket using the configured framing strategy.
     * This method is synchronized to ensure that only one thread writes to the socket at a time.
     *
     * @param message The byte array containing the message to send.
     * @return A {@code Mono<Void>} that completes when the message has been written to the socket.
     *         Any IOException during writing will be propagated as an UncheckedIOException.
     */
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

        /**
     * Broadcasts a message to all currently active TCP sessions.
     *
     * @param message The byte array containing the message to be broadcast.
     */
    public void broadcast(byte[] message) {
        for (TcpSession s : sessions.values()) {
            s.send(message).subscribe();
        }
    }

        /**
     * Sets the session map for this object.
     *
     * @param sessions A map of session IDs to TcpSession objects.
     */
    public void setSessions(Map<String, TcpSession> sessions) {
        this.sessions = sessions;
    }

        /**
     * Updates the last keep-alive timestamp to the current time.
     *
     * This method is typically called to indicate that the resource or connection
     * is still active and should be kept alive.
     */
    @Override
    public void touch() {
        lastKeepAlive.set(System.currentTimeMillis());
    }

        /**
     * Gets the timestamp of the last keep-alive signal received.
     *
     * @return The timestamp of the last keep-alive signal, in milliseconds since the epoch.
     */
    @Override
    public long getLastKeepAlive() {
        return lastKeepAlive.get();
    }

        /**
     * Checks if the socket is currently alive.
     *
     * @return true if the socket is open and alive, false otherwise.
     */
    @Override
    public boolean isAlive() {
        return !socket.isClosed();
    }

        /**
     * Retrieves the ID of the current session.
     *
     * @return The session ID as a String.
     */
    @Override
    public String getId() {
        return sessionId;
    }

        /**
     * Retrieves the remote address that this socket is connected to.
     *
     * @return The remote socket address, or null if the socket is not connected.
     */
    @Override
    public SocketAddress getRemoteAddress() {
        return socket.getRemoteSocketAddress();
    }

        /**
     * Returns the attributes associated with this object.
     *
     * @return A map containing the attributes, where the key is the attribute name (String)
     *         and the value is the attribute value (Object).
     */
    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }

        /**
     * Closes the socket.
     *
     * @throws IOException if an I/O error occurs when closing the socket.
     */
    @Override
    public void close() throws IOException {
        socket.close();
    }

        /**
     * Asynchronously closes this resource.
     *
     * <p>This method wraps the synchronous {@link #close()} method in a {@link Mono} to allow for
     * non-blocking closure. Any {@link IOException} thrown by the synchronous close method is
     * wrapped in an {@link UncheckedIOException} and propagated through the {@link Mono}.
     *
     * @return A {@link Mono} that completes when the resource is closed or emits an error if an
     *     {@link IOException} occurs during closure.
     */
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
