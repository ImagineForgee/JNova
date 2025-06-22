package jnova.core.events;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  A simple event bus implementation using Reactor's Sinks and Flux.
 *
 *  This class allows components to publish and subscribe to events of specific types.
 *  It uses a concurrent map to store sinks for each event type, ensuring thread-safe event handling.
 */
public class EventBus {
    private final Map<Class<?>, Sinks.Many<?>> sinks = new ConcurrentHashMap<>();

        /**
     * Provides a reactive stream (`Flux`) of events for a specific event type.
     *
     * @param <T>       The type of event to listen for, extending {@link Event}.
     * @param eventType The `Class` object representing the event type.
     * @return A `Flux` that emits events of the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> Flux<T> onEvent(Class<T> eventType) {
        return ((Sinks.Many<T>) sinks
                .computeIfAbsent(eventType, key -> Sinks.many().multicast().onBackpressureBuffer()))
                .asFlux();
    }

        /**
     * Emits an event to the appropriate sink based on its type.
     *
     * This method retrieves or creates a {@link Sinks.Many} sink for the event's class and emits the event to it.
     * The sink is created using a {@link Sinks.Many} with multicast and backpressure buffer strategies.
     *
     * @param <T>   The type of the event, which must extend {@link Event}.
     * @param event The event to emit.
     *
     * @throws ClassCastException if the sink is not of the correct type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void emit(T event) {
        ((Sinks.Many<T>) sinks
                .computeIfAbsent(event.getClass(), key -> Sinks.many().multicast().onBackpressureBuffer()))
                .tryEmitNext(event);
    }
}
