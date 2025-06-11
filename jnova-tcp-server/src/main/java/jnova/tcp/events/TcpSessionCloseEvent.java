package jnova.tcp.events;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;
import jnova.tcp.TcpSession;

public class TcpSessionCloseEvent extends AbstractEvent {
    private final TcpSession session;
    private final String reason;

    public TcpSessionCloseEvent(EventBuilder<TcpSessionCloseEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.session = builder.get("session", TcpSession.class);
        this.reason = builder.get("reason", String.class);
    }

    public TcpSession getSession() {
        return session;
    }

    public String getReason() {
        return reason;
    }
}
