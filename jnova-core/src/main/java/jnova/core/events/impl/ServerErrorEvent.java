package jnova.core.events.impl;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;

import java.io.IOException;
import java.net.InetAddress;

public class ServerErrorEvent extends AbstractEvent {
    private final Throwable error;
    private final int port;
    private final InetAddress address;

    public ServerErrorEvent(EventBuilder<ServerErrorEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.error = builder.get("error", IOException.class);
        this.port = builder.get("port", Integer.class);
        this.address = builder.get("address", InetAddress.class);
    }

    public Throwable getError() {
        return error;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
    }
}

