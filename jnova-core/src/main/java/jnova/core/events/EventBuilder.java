package jnova.core.events;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * A builder class for creating instances of {@link AbstractEvent} and its subclasses.
 *
 * <p>This class provides a fluent interface for setting various properties of an event, such as
 * its type, source, correlation ID, timestamp, and custom properties. It uses a constructor
 * function to create the final event object.
 *
 * @param <T> The type of {@link AbstractEvent} to build.
 */
public class EventBuilder<T extends AbstractEvent> {
    private EventType type;
    private Object source;
    private final Map<String, Object> properties = new HashMap<>();
    private UUID correlationId = UUID.randomUUID();
    private Instant timestamp = Instant.now();

    private Function<EventBuilder<T>, T> constructor;

        /**
     * Constructs an {@code EventBuilder} with the given constructor function.
     *
     * @param constructor A function that takes an {@code EventBuilder} and returns an instance of type {@code T}.
     */
    private EventBuilder(Function<EventBuilder<T>, T> constructor) {
        this.constructor = constructor;
    }

        /**
     * Creates an EventBuilder for a specific event type.
     *
     * @param <T> The type of the event being built, which must extend AbstractEvent.
     * @param type The EventType associated with the event.
     * @param constructor A function that takes an EventBuilder and returns an instance of the event.
     * @return A new EventBuilder instance configured for the specified event type and constructor.
     */
    public static <T extends AbstractEvent> EventBuilder<T> ofType(EventType type, Function<EventBuilder<T>, T> constructor) {
        EventBuilder<T> builder = new EventBuilder<>(constructor);
        builder.type = type;
        return builder;
    }

        /**
     * Sets the source of the event.
     *
     * @param source The source object that originated the event.
     * @return This {@code EventBuilder} instance for chaining.
     */
    public EventBuilder<T> fromSource(Object source) {
        this.source = source;
        return this;
    }

        /**
     * Sets the correlation ID for the event.
     *
     * @param correlationId The UUID to use as the correlation ID.
     * @return This {@link EventBuilder} instance for chaining.
     */
    public EventBuilder<T> withCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
        return this;
    }

        /**
     * Sets the timestamp for the event.
     *
     * @param timestamp The timestamp to set for the event.
     * @return This {@code EventBuilder} instance for chaining.
     */
    public EventBuilder<T> withTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

        /**
     * Retrieves the type of this event.
     *
     * @return The {@link EventType} associated with this event.
     */
    public EventType getType() {
        return type;
    }

        /**
       * Returns the source object.
       *
       * @return the source object
       */
    public Object getSource() {
        return source;
    }

        /**
     * Retrieves the correlation ID.
     *
     * @return The correlation ID.
     */
    public UUID getCorrelationId() {
        return correlationId;
    }

        /**
     * Returns the timestamp.
     *
     * @return the timestamp.
     */
    public Instant getTimestamp() {
        return timestamp;
    }

        /**
     * Adds a property to the event with the given key and value.
     *
     * @param key The key of the property.
     * @param value The value of the property.
     * @param <V> The type of the value.
     * @return This {@link EventBuilder} instance for chaining.
     */
    public <V> EventBuilder<T> with(String key, V value) {
        properties.put(key, value);
        return this;
    }

        /**
     * Retrieves a value from the properties map, casting it to the specified class.
     *
     * @param <V>   The type of the value to retrieve.
     * @param key   The key associated with the value.
     * @param clazz The class to cast the value to.
     * @return The value associated with the key, cast to the specified class,
     *         or null if the key is not found or the value cannot be cast.
     */
    public <V> V get(String key, Class<V> clazz) {
        Object value = properties.get(key);
        if (value == null) return null;
        return clazz.cast(value);
    }

        /**
     * Builds a new instance of the object using the provided constructor.
     *
     * @return A new instance of the object.
     */
    public T build() {
        return constructor.apply(this);
    }
}
