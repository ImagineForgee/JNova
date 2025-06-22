package jnova.tcp;

/**
 * Represents a TCP response containing a byte array body.
 */
public class TcpResponse {
    private final byte[] body;

        /**
     * Constructs a TcpResponse with the given body.
     *
     * @param body The byte array representing the response body.
     */
    public TcpResponse(byte[] body) {
        this.body = body;
    }

        /**
     * Returns the byte array representation of the body.
     *
     * @return The byte array representing the body.
     */
    public byte[] getBytes() {
        return body;
    }
}
