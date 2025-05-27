package jnova.tcp;

public class TcpResponse {
    private final String body;

    public TcpResponse(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
