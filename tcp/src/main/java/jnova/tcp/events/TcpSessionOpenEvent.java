package jnova.tcp.events;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;
import jnova.tcp.TcpSession;

public class TcpSessionOpenEvent extends AbstractEvent {
    private final TcpSession session;

    public TcpSessionOpenEvent(EventBuilder<TcpSessionOpenEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.session = builder.get("session", TcpSession.class);
    }

    public TcpSession getSession() {
        return session;
    }
}
