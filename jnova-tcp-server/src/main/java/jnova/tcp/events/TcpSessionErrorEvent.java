package jnova.tcp.events;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;
import jnova.tcp.TcpSession;

/**
 * An event that represents an error occurring within a TCP session.
 *
 * This event contains information about the session in which the error occurred,
 * as well as the error itself.
 */
public class TcpSessionErrorEvent extends AbstractEvent {
    private final TcpSession session;
    private final Throwable error;

        /**
     * Constructs a {@code TcpSessionErrorEvent}.
     *
     * @param builder The builder used to construct the event.
     */
    public TcpSessionErrorEvent(EventBuilder<TcpSessionErrorEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.session = builder.get("session", TcpSession.class);
        this.error = builder.get("error", Throwable.class);
    }

        /**
     * Retrieves the current TCP session.
     *
     * @return The TCP session associated with this object.
     */
    public TcpSession getSession() {
        return session;
    }

        /**
       * Retrieves the error associated with this object.
       *
       * @return The Throwable object representing the error, or null if no error occurred.
       */
    public Throwable getError() {
        return error;
    }
}
