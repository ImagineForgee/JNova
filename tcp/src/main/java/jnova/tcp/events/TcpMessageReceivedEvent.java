package jnova.tcp.events;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;
import jnova.tcp.TcpSession;

public class TcpMessageReceivedEvent extends AbstractEvent {
    private final TcpSession session;
    private final byte[] message;

    public TcpMessageReceivedEvent(EventBuilder<TcpMessageReceivedEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.session = builder.get("session", TcpSession.class);
        this.message = builder.get("message", byte[].class);
    }

    public TcpSession getSession() {
        return session;
    }

    public byte[] getMessage() {
        return message;
    }
}
