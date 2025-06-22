package jnova.tcp.handler;

import jnova.tcp.TcpResponse;
import jnova.tcp.request.TcpBinaryRequest;
import jnova.tcp.routing.commands.TcpCommandDispatcher;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Handles incoming TCP requests by dispatching commands to the appropriate handler.
 *
 * This class receives TCP binary requests, extracts the command and arguments,
 * and dispatches them to the {@link TcpCommandDispatcher} for processing.
 * Any errors during dispatching are caught and logged.
 */
public class CommandRequestHandler implements TcpRequestHandler {
    private final TcpCommandDispatcher dispatcher;

        /**
     * Constructs a CommandRequestHandler with a given dispatcher.
     *
     * @param dispatcher The TcpCommandDispatcher used to dispatch commands.
     */
    public CommandRequestHandler(TcpCommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

        /**
     * Handles incoming TCP binary requests, dispatches commands, and manages errors.
     *
     * <p>This method receives a {@link TcpBinaryRequest}, extracts the command and arguments from the payload,
     * and dispatches the command to the appropriate handler using a {@link TcpCommandDispatcher}.
     * Any errors that occur during command dispatch are caught and logged.
     *
     * @param request The TCP binary request to handle.
     * @return A {@link Mono} that completes when the command has been dispatched or when an error occurs.
     */
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
