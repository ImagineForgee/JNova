package jnova.core.events;

import java.time.Instant;
import java.util.UUID;

public interface Event {
    /**
     * The time at which the event was created/emitted.
     */
    Instant getTimestamp();

    /**
     * The source or origin of the event (could be the server, session, etc.).
     */
    default Object getSource() {
        return null;
    }

    default UUID correlationId() {
        return null;
    }

    default EventType eventType() {
        return null;
    }
}
