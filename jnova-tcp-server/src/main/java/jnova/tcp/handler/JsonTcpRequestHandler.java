package jnova.tcp.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jnova.tcp.TcpResponse;
import jnova.tcp.request.TcpBinaryRequest;
import jnova.tcp.routing.commands.TcpCommandDispatcher;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonTcpRequestHandler implements TcpRequestHandler {
    private final Gson gson = new Gson();
    private final TcpCommandDispatcher dispatcher;

    public JsonTcpRequestHandler(TcpCommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Mono<TcpResponse> handle(TcpBinaryRequest request) {
        try {
            String msg = new String(request.getData());
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> json = gson.fromJson(msg, mapType);

            String type = (String) json.get("type");
            if (type == null) {
                return errorResponse("Missing type field");
            }

            if ("command".equalsIgnoreCase(type)) {
                String cmd = (String) json.get("command");
                List<?> rawArgs = (List<?>) json.get("args");
                String[] args = rawArgs != null
                        ? rawArgs.stream().map(Object::toString).toArray(String[]::new)
                        : new String[0];
                dispatcher.dispatch(cmd.toUpperCase(), request.getSession(), args);
                return Mono.empty();
            }

            return errorResponse("Unknown type: " + type);

        } catch (Exception e) {
            return errorResponse("Malformed packet");
        }
    }

    private Mono<TcpResponse> errorResponse(String message) {
        Map<String, String> errorResponse = Map.of(
                "type", "error",
                "message", message
        );
        return Mono.just(new TcpResponse((gson.toJson(errorResponse) + "\n").getBytes()));
    }

}

