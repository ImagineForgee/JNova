package jnova.tcp.request;

import jnova.tcp.TcpSession;

public class TcpBinaryRequest {
    private final byte[] data;
    private final TcpSession session;

    public TcpBinaryRequest(byte[] data, TcpSession session) {
        this.data = data;
        this.session = session;
    }

    public byte[] getData() {
        return data;
    }

    public TcpSession getSession() {
        return session;
    }
}

