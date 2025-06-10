package jnova.core.events;

import java.util.UUID;
import java.time.Instant;

public abstract class AbstractEvent implements Event {
    private final UUID correlationId;
    private final EventType type;
    private final Instant timestamp;
    private final Object source;

    protected AbstractEvent(EventType type) {
        this(type, UUID.randomUUID(), Instant.now());
    }

    protected AbstractEvent(EventType type, UUID correlationId) {
        this(type, correlationId, Instant.now());
    }

    protected AbstractEvent(EventType type, UUID correlationId, Instant timestamp) {
        this(type, correlationId, timestamp, null);
    }

    protected AbstractEvent(EventType type, UUID correlationId, Instant timestamp, Object source) {
        this.type = type;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
        this.source = source;
    }


    @Override
    public UUID correlationId() {
        return correlationId;
    }

    @Override
    public EventType eventType() {
        return type;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public Object getSource() {
        return source;
    }
}

