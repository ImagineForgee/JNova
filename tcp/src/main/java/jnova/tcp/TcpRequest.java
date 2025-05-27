package jnova.tcp;

public class TcpRequest {
    private final String body;
    private final TcpSession session;

    public TcpRequest(String body, TcpSession session) {
        this.body = body;
        this.session = session;
    }

    public String getBody() {
        return body;
    }

    public TcpSession getSession() {
        return session;
    }
}
