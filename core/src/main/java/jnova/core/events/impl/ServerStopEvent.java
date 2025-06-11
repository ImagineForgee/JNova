package jnova.core.events.impl;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;

public class ServerStopEvent extends AbstractEvent {
    public ServerStopEvent(EventBuilder<ServerStopEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
    }
}
