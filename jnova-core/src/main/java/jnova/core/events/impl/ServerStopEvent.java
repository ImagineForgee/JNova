package jnova.core.events.impl;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;

/**
 * Represents an event that signifies the stopping of a server.
 *
 * This event extends {@link AbstractEvent} and contains information about when a server stopped.
 */
public class ServerStopEvent extends AbstractEvent {
        /**
     * Constructs a {@code ServerStopEvent} with the provided builder.
     *
     * @param builder The builder containing the event's properties.
     */
    public ServerStopEvent(EventBuilder<ServerStopEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
    }
}
