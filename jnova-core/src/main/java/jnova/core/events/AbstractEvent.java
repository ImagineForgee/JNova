package jnova.core.events;

import java.util.UUID;
import java.time.Instant;

/**
 * An abstract base class for implementing {@link Event}s.
 *
 * <p>Provides default implementations for common event attributes such as correlation ID, event
 * type, timestamp, and source. Subclasses should extend this class to create specific event
 * types.</p>
 */
public abstract class AbstractEvent implements Event {
    private final UUID correlationId;
    private final EventType type;
    private final Instant timestamp;
    private final Object source;

        /**
     * Constructs an {@code AbstractEvent} with the given type, a randomly generated UUID, and the current timestamp.
     *
     * @param type The {@link EventType} of the event.
     */
    protected AbstractEvent(EventType type) {
        this(type, UUID.randomUUID(), Instant.now());
    }

        /**
     * Constructs an {@code AbstractEvent} with the given type and correlation ID, using the current time.
     *
     * @param type The type of the event.
     * @param correlationId The correlation ID associated with the event.
     */
    protected AbstractEvent(EventType type, UUID correlationId) {
        this(type, correlationId, Instant.now());
    }

        /**
     * Constructs an {@code AbstractEvent} with the specified type, correlation ID, and timestamp.
     *
     * @param type          The event type.
     * @param correlationId The correlation ID for tracing events.
     * @param timestamp     The timestamp of when the event occurred.
     */
    protected AbstractEvent(EventType type, UUID correlationId, Instant timestamp) {
        this(type, correlationId, timestamp, null);
    }

        /**
         * Constructs an {@code AbstractEvent} with the specified type, correlation ID, timestamp, and source.
         *
         * @param type          The type of the event.
         * @param correlationId The unique identifier for correlating this event with related events.
         * @param timestamp     The timestamp indicating when the event occurred.
         * @param source        The source object that generated the event.
         */
    protected AbstractEvent(EventType type, UUID correlationId, Instant timestamp, Object source) {
        this.type = type;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
        this.source = source;
    }


        /**
     * Retrieves the correlation ID.
     *
     * @return The correlation ID as a UUID.
     */
    @Override
    public UUID correlationId() {
        return correlationId;
    }

        /**
       * Returns the event type.
       *
       * @return The event type.
       */
    @Override
    public EventType eventType() {
        return type;
    }

        /**
     * Returns the timestamp associated with this object.
     *
     * @return The timestamp as an {@link Instant}.
     */
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

        /**
     * Returns the source object.
     *
     * @return the source object
     */
    @Override
    public Object getSource() {
        return source;
    }
}

