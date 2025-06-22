package jnova.tcp.handler;

import jnova.tcp.TcpResponse;
import jnova.tcp.request.TcpBinaryRequest;
import jnova.tcp.request.TcpRequest;
import reactor.core.publisher.Mono;

/**
 * Interface for handling TCP requests.
 *
 * <p>Defines methods for processing both binary and generic TCP requests, allowing
 * implementations to handle different types of incoming data.
 */
public interface TcpRequestHandler {
        /**
     * Handles a TCP binary request and returns a Mono emitting the response.
     *
     * @param request The TCP binary request to handle.
     * @return A Mono emitting the TCP response.
     */
    Mono<TcpResponse> handle(TcpBinaryRequest request);

        /**
         * Handles a TCP request and returns a TCP response.
         *
         * <p>This default implementation returns an error indicating that text requests are not supported.
         *
         * @param request The TCP request to handle.
         * @return A Mono emitting a TcpResponse, or an error if text requests are not supported.
         */
    default Mono<TcpResponse> handle(TcpRequest request) {
        return Mono.error(new UnsupportedOperationException("Text requests not supported"));
    }
}
