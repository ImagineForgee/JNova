package jnova.tcp.events;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;
import jnova.tcp.TcpSession;

/**
 * An event that is triggered when a TCP session is opened.
 *
 * This event contains information about the newly opened TCP session.
 */
public class TcpSessionOpenEvent extends AbstractEvent {
    private final TcpSession session;

        /**
     * Constructs a TcpSessionOpenEvent from a builder.
     *
     * @param builder The builder containing the event's data.  Must provide the 'session' attribute
     *                as a {@link TcpSession} instance.
     */
    public TcpSessionOpenEvent(EventBuilder<TcpSessionOpenEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.session = builder.get("session", TcpSession.class);
    }

        /**
     * Retrieves the current TCP session.
     *
     * @return The TCP session object.
     */
    public TcpSession getSession() {
        return session;
    }
}
