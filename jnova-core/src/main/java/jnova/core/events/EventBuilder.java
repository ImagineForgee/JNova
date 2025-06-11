package jnova.core.events;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class EventBuilder<T extends AbstractEvent> {
    private EventType type;
    private Object source;
    private final Map<String, Object> properties = new HashMap<>();
    private UUID correlationId = UUID.randomUUID();
    private Instant timestamp = Instant.now();

    private Function<EventBuilder<T>, T> constructor;

    private EventBuilder(Function<EventBuilder<T>, T> constructor) {
        this.constructor = constructor;
    }

    public static <T extends AbstractEvent> EventBuilder<T> ofType(EventType type, Function<EventBuilder<T>, T> constructor) {
        EventBuilder<T> builder = new EventBuilder<>(constructor);
        builder.type = type;
        return builder;
    }

    public EventBuilder<T> fromSource(Object source) {
        this.source = source;
        return this;
    }

    public EventBuilder<T> withCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public EventBuilder<T> withTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public EventType getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public <V> EventBuilder<T> with(String key, V value) {
        properties.put(key, value);
        return this;
    }

    public <V> V get(String key, Class<V> clazz) {
        Object value = properties.get(key);
        if (value == null) return null;
        return clazz.cast(value);
    }

    public T build() {
        return constructor.apply(this);
    }
}
