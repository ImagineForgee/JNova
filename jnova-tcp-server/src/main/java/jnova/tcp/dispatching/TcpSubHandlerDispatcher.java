package jnova.tcp.dispatching;

import com.google.gson.Gson;
import jnova.core.util.GsonFactory;
import jnova.core.validation.ParameterValidator;
import jnova.tcp.TcpResponse;
import jnova.tcp.TcpSession;
import jnova.tcp.service.DispatcherReportService;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dispatches incoming TCP requests to the appropriate handler based on the message type and command.
 *
 * This class is responsible for routing TCP messages to the correct {@link TcpTypeHandler},
 * validating parameters, executing middleware, and handling exceptions.
 */
public class TcpSubHandlerDispatcher {
    private final TcpTypeHandler registry;
    private final ParameterValidator validator;
    private final List<TcpMiddleware> middleware;
    private final Gson gson = GsonFactory.get();

        /**
     * Constructs a TcpSubHandlerDispatcher.
     *
     * @param registry The registry containing TCP type handlers.
     * @param validator The validator for validating parameters.
     * @param middleware A list of TCP middleware to be applied. If null or empty, a default no-op middleware is used.
     */
    public TcpSubHandlerDispatcher(TcpTypeHandler registry,
                                   ParameterValidator validator,
                                   List<TcpMiddleware> middleware) {
        this.registry = registry;
        this.validator = validator;
        this.middleware = middleware != null && !middleware.isEmpty()
                ? middleware
                : List.of(new TcpMiddleware() {});

        for (TcpMiddleware mw : this.middleware) {
            DispatcherReportService.registerMiddleware(mw.getClass());
        }
    }

        /**
     * Dispatches a TCP request to the appropriate handler based on the command specified in the JSON payload.
     *
     * This method processes the incoming request, executes middleware, finds the appropriate handler,
     * validates arguments, invokes the handler, and handles any exceptions that may occur during the process.
     *
     * @param type The type of the request.
     * @param json A map containing the JSON payload of the request.  The map should contain a "command" key
     *             that specifies the command to be dispatched.
     * @param session The TCP session associated with the request.
     * @return A {@link Mono} emitting a {@link TcpResponse} representing the result of the dispatch.
     *         Returns an error response if any errors occur during dispatch, such as missing command,
     *         validation failure, invocation failure, or no handler found.  An empty {@link Mono} is
     *         returned upon successful completion.
     */
    public Mono<TcpResponse> dispatch(String type, Map<String, Object> json, TcpSession session) {
        for (TcpMiddleware mw : middleware) {
            try {
                mw.beforeDispatch(json, session);
            } catch (Throwable t) {
                mw.onException(t, json, session);
            }
        }

        try {
            Object keyVal = json.get("command");
            if (keyVal == null)
                return errorResponse("Missing 'command' field", List.of());

            String command = keyVal.toString();

            return registry.findHandler(type, "command", command)
                    .map(handler -> {
                        try {
                            Object[] args = registry.resolveArguments(handler.method(), json, session);
                            List<String> violations = validator.validateParameters(args, handler.method().getParameters());
                            if (!violations.isEmpty()) {
                                return errorResponse("Validation failed", violations);
                            }

                            handler.method().invoke(handler.instance(), args);

                            for (TcpMiddleware mw : middleware) {
                                try {
                                    mw.afterDispatch(json, session);
                                } catch (Throwable mwErr) {
                                    mw.onException(mwErr, json, session);
                                }
                            }

                            return Mono.<TcpResponse>empty();

                        } catch (Throwable t) {
                            for (TcpMiddleware mw : middleware) {
                                mw.onException(t, json, session);
                            }

                            String trace = formatStackTrace(t);
                            return errorResponse("Invocation failed: " + trace, List.of());
                        }
                    })
                    .orElseGet(() -> {
                        RuntimeException noHandlerEx = new RuntimeException("No handler found for command: " + command);
                        for (TcpMiddleware mw : middleware) {
                            mw.onException(noHandlerEx, json, session);
                        }
                        return errorResponse(noHandlerEx.getMessage(), List.of());
                    });

        } catch (Throwable t) {
            for (TcpMiddleware mw : middleware) {
                mw.onException(t, json, session);
            }
            return errorResponse("Dispatcher error: " + t.getMessage(), List.of());
        }
    }

        /**
     * Creates an error response containing a validation error message and optional error details.
     *
     * @param message The main error message.
     * @param errors  A list of specific error messages (can be empty).
     * @return A Mono emitting a TcpResponse containing the error as a JSON string.
     */
    private Mono<TcpResponse> errorResponse(String message, List<String> errors) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "validation_error");
        payload.put("message", message);
        if (!errors.isEmpty()) payload.put("errors", errors);

        String jsonStr = gson.toJson(payload) + "\n";
        return Mono.just(new TcpResponse(jsonStr.getBytes()));
    }

        /**
     * Formats a stack trace into a human-readable string.
     *
     * @param t The throwable (exception or error) whose stack trace is to be formatted.
     * @return A string representation of the stack trace. Includes the exception type,
     *         message, and each element of the stack trace on a new line, indented for readability.
     */
    private String formatStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getSimpleName()).append(": ").append(t.getMessage()).append("\n");
        for (StackTraceElement el : t.getStackTrace()) {
            sb.append("  at ").append(el.toString()).append("\n");
        }
        return sb.toString();
    }
}
