package jnova.tcp.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jnova.core.util.GsonFactory;
import jnova.tcp.TcpResponse;
import jnova.tcp.dispatching.DispatcherContext;
import jnova.tcp.request.TcpBinaryRequest;
import jnova.tcp.routing.commands.TcpCommandDispatcher;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * A TCP request handler that processes JSON structure requests.
 *
 * This class implements the {@link TcpRequestHandler} interface and handles incoming
 * {@link TcpBinaryRequest} by parsing the JSON data, dispatching the request to a
 * sub-dispatcher, and applying middleware for pre- and post-processing. It utilizes
 * Gson for JSON parsing and a {@link DispatcherContext} for managing dispatching and
 * middleware.
 */
public class JsonStructureRequestHandler implements TcpRequestHandler {
    private final DispatcherContext ctx;
    private final Gson gson = GsonFactory.get();
    private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

        /**
     * Constructs a {@code JsonStructureRequestHandler} with the given dispatcher context.
     *
     * @param ctx The dispatcher context to use for handling requests.
     */
    public JsonStructureRequestHandler(DispatcherContext ctx) {
        this.ctx = ctx;
    }

        /**
     * Handles a TCP binary request.
     *
     * <p>This method parses the JSON from the request, extracts the type, applies middleware,
     * dispatches the request to a sub-dispatcher, applies middleware after dispatch, and returns
     * a Mono of TcpResponse.</p>
     *
     * @param request The TCP binary request to handle.
     * @return A Mono of TcpResponse representing the result of handling the request.
     */
    @Override
    public Mono<TcpResponse> handle(TcpBinaryRequest request) {
        Map<String,Object> json = parseJson(request);
        String type = (String)json.get("type");

        ctx.middleware.forEach(mw -> mw.beforeDispatch(json, request.getSession()));

        Mono<TcpResponse> response = ctx.subDispatcher.dispatch(type, json, request.getSession());
        response = response.doOnSuccess(r -> ctx.middleware.forEach(mw -> mw.afterDispatch(json, request.getSession())));

        return response;
    }

        /**
     * Parses a JSON string from a TCP binary request into a Map.
     *
     * @param request The TCP binary request containing the JSON data.
     * @return A Map representing the parsed JSON data, or null if parsing fails.
     */
    private Map<String, Object> parseJson(TcpBinaryRequest request) {
        String msg = new String(request.getData());
        return gson.fromJson(msg, mapType);
    }
}

