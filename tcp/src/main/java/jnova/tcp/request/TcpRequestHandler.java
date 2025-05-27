package jnova.tcp.request;

import jnova.tcp.TcpResponse;
import reactor.core.publisher.Mono;

public interface TcpRequestHandler {
    Mono<TcpResponse> handle(TcpRequest request);

    default Mono<TcpResponse> handle(TcpBinaryRequest request) {
        return Mono.error(new UnsupportedOperationException("Binary requests not supported"));
    }
}
