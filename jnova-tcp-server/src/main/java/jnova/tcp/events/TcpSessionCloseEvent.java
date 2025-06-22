package jnova.tcp.events;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;
import jnova.tcp.TcpSession;

/**
 * Represents an event that is triggered when a TCP session is closed.
 *
 * This event contains information about the closed session, including the session object itself
 * and the reason for closure.  It extends {@link AbstractEvent} and uses an {@link EventBuilder}
 * for construction.
 */
public class TcpSessionCloseEvent extends AbstractEvent {
    private final TcpSession session;
    private final String reason;

        /**
     * Constructs a {@code TcpSessionCloseEvent} from a builder.
     *
     * @param builder The builder containing the event's properties.  Must include
     *                the session and reason for closing.
     */
    public TcpSessionCloseEvent(EventBuilder<TcpSessionCloseEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.session = builder.get("session", TcpSession.class);
        this.reason = builder.get("reason", String.class);
    }

        /**
     * Returns the current TCP session.
     *
     * @return The TCP session.
     */
    public TcpSession getSession() {
        return session;
    }

        /**
     * Gets the reason.
     *
     * @return The reason.
     */
    public String getReason() {
        return reason;
    }
}
