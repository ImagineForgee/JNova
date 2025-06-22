package jnova.tcp.request;

import jnova.tcp.TcpSession;

/**
 * Represents a TCP request received by the server.
 *
 * <p>Encapsulates the request body and the associated TCP session.
 */
public class TcpRequest {
    private final String body;
    private final TcpSession session;

        /**
         * Constructs a new TcpRequest with the given body and session.
         *
         * @param body The body of the TCP request.
         * @param session The TCP session associated with the request.
         */
    public TcpRequest(String body, TcpSession session) {
        this.body = body;
        this.session = session;
    }

        /**
     * Retrieves the body of the object.
     *
     * @return The body as a String.
     */
    public String getBody() {
        return body;
    }

        /**
     * Returns the current TCP session.
     *
     * @return The TCP session associated with this connection.
     */
    public TcpSession getSession() {
        return session;
    }
}
