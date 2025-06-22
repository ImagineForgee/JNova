package jnova.tcp.events;

import jnova.core.events.AbstractEvent;
import jnova.core.events.EventBuilder;
import jnova.tcp.TcpSession;

/**
 * Event triggered when a TCP message is received.
 *
 * This event contains information about the TCP session and the received message.
 */
public class TcpMessageReceivedEvent extends AbstractEvent {
    private final TcpSession session;
    private final byte[] message;

        /**
     * Constructs a {@code TcpMessageReceivedEvent} from a builder.
     *
     * @param builder The builder containing the event's properties.
     */
    public TcpMessageReceivedEvent(EventBuilder<TcpMessageReceivedEvent> builder) {
        super(builder.getType(), builder.getCorrelationId(), builder.getTimestamp(), builder.getSource());
        this.session = builder.get("session", TcpSession.class);
        this.message = builder.get("message", byte[].class);
    }

        /**
     * Retrieves the current TCP session.
     *
     * @return The TCP session object.
     */
    public TcpSession getSession() {
        return session;
    }

        /**
     * Retrieves the message.
     *
     * @return The message as a byte array.
     */
    public byte[] getMessage() {
        return message;
    }
}
