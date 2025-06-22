package jnova.core.events.impl;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Represents an event that occurs when a server encounters an error.
 *
 * This event contains information about the error, the port the server was running on,
 * and the address of the server. It extends {@link AbstractEvent}.
 */
public class ServerErrorEvent extends AbstractEvent {
    private final Throwable error;
    private final int port;
    private final InetAddress address;

        /**
     * Constructs a {@code ServerErrorEvent}.
     *
     * @param builder The builder containing the event's data.  It must provide the type,
     *                correlation ID, timestamp, source, error, port, and address.
     */
    public ServerErrorEvent(EventBuilder<ServerErrorEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.error = builder.get("error", IOException.class);
        this.port = builder.get("port", Integer.class);
        this.address = builder.get("address", InetAddress.class);
    }

        /**
       * Retrieves the error.
       *
       * @return The error; may be null.
       */
    public Throwable getError() {
        return error;
    }

        /**
     * Returns the port number.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }

        /**
         * Returns the IP address.
         *
         * @return the IP address
         */
    public InetAddress getAddress() {
        return address;
    }
}

