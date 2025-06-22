package jnova.tcp.request;

import jnova.tcp.TcpSession;

import java.nio.charset.StandardCharsets;

/**
 * Represents a TCP binary request containing data, session, and payload information.
 *
 *  This class encapsulates the raw byte data received, the associated TCP session,
 *  and a string representation of the data (the payload).
 */
public class TcpBinaryRequest {
    private final byte[] data;
    private final TcpSession session;
    private final String payload;

        /**
         * Constructs a TcpBinaryRequest with the given data and session.
         *
         * @param data    The binary data of the request.
         * @param session The TCP session associated with the request.
         */
    public TcpBinaryRequest(byte[] data, TcpSession session) {
        this.data = data;
        this.session = session;
        this.payload = new String(data).trim();
    }

        /**
     * Retrieves the internal data array.
     *
     * @return A byte array containing the stored data.
     */
    public byte[] getData() { return data; }
        /**
     * Returns the TCP session associated with this object.
     *
     * @return The TCP session.
     */
    public TcpSession getSession() { return session; }
        /**
     * Retrieves the payload.
     *
     * @return the payload string
     */
    public String getPayload() { return payload; }
}

