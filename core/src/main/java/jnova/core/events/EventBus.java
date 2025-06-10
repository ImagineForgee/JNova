package jnova.core.events;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private final Map<Class<?>, Sinks.Many<?>> sinks = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> Flux<T> onEvent(Class<T> eventType) {
        return ((Sinks.Many<T>) sinks
                .computeIfAbsent(eventType, key -> Sinks.many().multicast().onBackpressureBuffer()))
                .asFlux();
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void emit(T event) {
        ((Sinks.Many<T>) sinks
                .computeIfAbsent(event.getClass(), key -> Sinks.many().multicast().onBackpressureBuffer()))
                .tryEmitNext(event);
    }
}
