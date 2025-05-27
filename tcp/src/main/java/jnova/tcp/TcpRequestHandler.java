package jnova.tcp;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface TcpRequestHandler {
    Mono<TcpResponse> handle(TcpRequest request);
}
