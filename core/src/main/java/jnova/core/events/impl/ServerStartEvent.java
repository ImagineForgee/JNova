package jnova.core.events.impl;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;

import java.net.InetAddress;

public class ServerStartEvent extends AbstractEvent {
    private final int port;
    private final InetAddress address;

    public ServerStartEvent(EventBuilder<ServerStartEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.port = builder.get("port", Integer.class);
        this.address = builder.get("address", InetAddress.class);
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
    }
}
