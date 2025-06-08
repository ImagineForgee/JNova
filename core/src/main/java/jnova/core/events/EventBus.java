package jnova.core.events;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private final Map<Class<?>, Sinks.Many<Object>> sinks = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Flux<T> onEvent(Class<T> eventType) {
        return (Flux<T>) sinks
                .computeIfAbsent(eventType, key -> Sinks.many().multicast().onBackpressureBuffer())
                .asFlux();
    }

    public <T> void emit(T event) {
        sinks.computeIfAbsent(event.getClass(), key -> Sinks.many().multicast().onBackpressureBuffer())
                .tryEmitNext(event);
    }
}
