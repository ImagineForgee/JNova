package jnova.tcp.request;

import jnova.tcp.TcpResponse;
import reactor.core.publisher.Mono;

public interface TcpRequestHandler {
    Mono<TcpResponse> handle(TcpBinaryRequest request);

    default Mono<TcpResponse> handle(TcpRequest request) {
        return Mono.error(new UnsupportedOperationException("Text requests not supported"));
    }
}
