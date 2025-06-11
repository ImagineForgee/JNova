package jnova.tcp.events;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;
import jnova.tcp.TcpSession;

public class TcpSessionErrorEvent extends AbstractEvent {
    private final TcpSession session;
    private final Throwable error;

    public TcpSessionErrorEvent(EventBuilder<TcpSessionErrorEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.session = builder.get("session", TcpSession.class);
        this.error = builder.get("error", Throwable.class);
    }

    public TcpSession getSession() {
        return session;
    }

    public Throwable getError() {
        return error;
    }
}
