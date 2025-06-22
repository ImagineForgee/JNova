package jnova.core.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an event that occurs within the system.
 *
 * An event encapsulates information about a specific occurrence,
 * including its timestamp, source, correlation ID, and type.
 */
public interface Event {
    /**
     * The time at which the event was created/emitted.
     */
        /**
     * Returns the current timestamp.
     *
     * @return the current timestamp
     */
    Instant getTimestamp();

    /**
     * The source or origin of the event (could be the server, session, etc.).
     */
        /**
         * Retrieves the source object.
         *
         * @return the source object, or null if no source is available
         */
    default Object getSource() {
        return null;
    }

        /**
     * Retrieves the correlation ID.
     *
     * @return The correlation ID, or null if not available.
     */
    default UUID correlationId() {
        return null;
    }

        /**
     * Returns the event type associated with this object.
     *
     * @return the {@link EventType} or null if no event type is associated.
     */
    default EventType eventType() {
        return null;
    }
}
