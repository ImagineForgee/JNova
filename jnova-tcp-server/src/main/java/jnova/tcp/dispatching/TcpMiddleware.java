package jnova.tcp.dispatching;

import jnova.tcp.TcpSession;

import java.util.Map;

/**
 *  Middleware interface for handling TCP session events and data dispatch.
 *
 *  <p>This interface provides default methods that can be overridden to intercept and process
 *  various events in a TCP session lifecycle, such as before and after data dispatch,
 *  exceptions, connection establishment, disconnection, timeouts, and protocol errors.
 */
public interface TcpMiddleware {
        /**
     * Executes actions before dispatching data.
     *
     * @param json The JSON data to be dispatched.
     * @param session The TCP session associated with the dispatch.
     */
    default void beforeDispatch(Map<String, Object> json, TcpSession session) {}
        /**
     * Executes after dispatching a message.
     *
     * <p>This default implementation does nothing.  Subclasses can override this method to
     * perform actions after a message has been dispatched, such as logging or cleanup.
     *
     * @param json The JSON message that was dispatched, as a Map.
     * @param session The TcpSession associated with the message.
     */
    default void afterDispatch(Map<String, Object> json, TcpSession session) {}
        /**
     * Handles exceptions that occur during message processing.
     *
     * <p>This method is called when an exception is caught while processing a message.
     * It allows for custom error handling and logging. The default implementation does nothing.</p>
     *
     * @param t       The throwable exception that occurred.
     * @param json    The JSON message (as a Map) that was being processed when the exception occurred.
     * @param session The TcpSession associated with the connection.
     */
    default void onException(Throwable t, Map<String, Object> json, TcpSession session) {}
        /**
     * Called when a TCP session is connected.
     *
     * @param session The connected TCP session.
     */
    default void onConnect(TcpSession session) {}
        /**
     * Handles the disconnection of a TCP session.
     *
     * <p>This default implementation does nothing. Subclasses can override this method to
     * implement custom disconnection handling logic.
     *
     * @param session The TCP session that has disconnected.
     */
    default void onDisconnect(TcpSession session) {}
        /**
     * Handles timeout events for a TCP session.
     *
     * This default implementation does nothing. Implementations can override this method
     * to provide custom timeout handling logic.
     *
     * @param session The TCP session that timed out.
     */
    default void onTimeout(TcpSession session) {}
        /**
     * Handles protocol errors that occur during TCP session communication.
     *
     * @param t The throwable representing the error.
     * @param session The TCP session where the error occurred.
     */
    default void onProtocolError(Throwable t, TcpSession session) {}
}
