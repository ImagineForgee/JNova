package jnova.tcp;

public class TcpResponse {
    private final byte[] body;

    public TcpResponse(byte[] body) {
        this.body = body;
    }

    public byte[] getBytes() {
        return body;
    }
}
