package jnova.tcp.events;

import java.net.InetAddress;
import java.time.Instant;

public class TcpServerStartEvent {
    private final int port;
    private final InetAddress address;
    private final Instant timestamp;

    public TcpServerStartEvent(int port, InetAddress address) {
        this.port = port;
        this.address = address;
        this.timestamp = Instant.now();
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
