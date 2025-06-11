package jnova.tcp.handler;

import jnova.tcp.TcpResponse;
import jnova.tcp.request.TcpBinaryRequest;
import jnova.tcp.routing.commands.TcpCommandDispatcher;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public class CommandRequestHandler implements TcpRequestHandler {
    private final TcpCommandDispatcher dispatcher;

    public CommandRequestHandler(TcpCommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Mono<TcpResponse> handle(TcpBinaryRequest request) {
        String msg = request.getPayload().trim();

        String[] parts = msg.split("\\s+");
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        return Mono.fromCallable(() -> {
            dispatcher.dispatch(command, request.getSession(), args);
            return null;
        }).onErrorResume(e -> {
            System.err.println("Dispatch error: " + e.getMessage());
            e.printStackTrace();
            return Mono.empty();
        }).then(Mono.empty());
    }
}
