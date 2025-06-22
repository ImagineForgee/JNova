package jnova.core.events.impl;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;

import java.net.InetAddress;

/**
 * Event representing the start of a server.
 *
 * <p>This event contains information about the port and address the server is listening on.
 */
public class ServerStartEvent extends AbstractEvent {
    private final int port;
    private final InetAddress address;

        /**
     * Constructs a {@code ServerStartEvent} from a builder.
     *
     * @param builder The {@link EventBuilder} containing the event's attributes.
     */
    public ServerStartEvent(EventBuilder<ServerStartEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.port = builder.get("port", Integer.class);
        this.address = builder.get("address", InetAddress.class);
    }

        /**
     * Retrieves the port number.
     *
     * @return The port number.
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
