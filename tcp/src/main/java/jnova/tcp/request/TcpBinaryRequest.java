package jnova.tcp.request;

import jnova.tcp.TcpSession;

import java.nio.charset.StandardCharsets;

public class TcpBinaryRequest {
    private final byte[] data;
    private final TcpSession session;
    private final String payload;

    public TcpBinaryRequest(byte[] data, TcpSession session) {
        this.data = data;
        this.session = session;
        this.payload = new String(data).trim();
    }

    public byte[] getData() { return data; }
    public TcpSession getSession() { return session; }
    public String getPayload() { return payload; }
}

