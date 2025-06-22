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

public class TcpSubHandlerDispatcher {
    private final TcpTypeHandlerRegistry registry;
    private final ParameterValidator validator;
    private final List<TcpMiddleware> middleware;
    private final Gson gson = GsonFactory.get();

    public TcpSubHandlerDispatcher(TcpTypeHandlerRegistry registry,
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

    private Mono<TcpResponse> errorResponse(String message, List<String> errors) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "validation_error");
        payload.put("message", message);
        if (!errors.isEmpty()) payload.put("errors", errors);

        String jsonStr = gson.toJson(payload) + "\n";
        return Mono.just(new TcpResponse(jsonStr.getBytes()));
    }

    private String formatStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getSimpleName()).append(": ").append(t.getMessage()).append("\n");
        for (StackTraceElement el : t.getStackTrace()) {
            sb.append("  at ").append(el.toString()).append("\n");
        }
        return sb.toString();
    }
}
