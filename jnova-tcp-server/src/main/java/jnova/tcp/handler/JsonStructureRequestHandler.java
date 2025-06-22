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

public class JsonStructureRequestHandler implements TcpRequestHandler {
    private final DispatcherContext ctx;
    private final Gson gson = GsonFactory.get();
    private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

    public JsonStructureRequestHandler(DispatcherContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Mono<TcpResponse> handle(TcpBinaryRequest request) {
        Map<String,Object> json = parseJson(request);
        String type = (String)json.get("type");

        ctx.middleware.forEach(mw -> mw.beforeDispatch(json, request.getSession()));

        Mono<TcpResponse> response = ctx.subDispatcher.dispatch(type, json, request.getSession());
        response = response.doOnSuccess(r -> ctx.middleware.forEach(mw -> mw.afterDispatch(json, request.getSession())));

        return response;
    }

    private Map<String, Object> parseJson(TcpBinaryRequest request) {
        String msg = new String(request.getData());
        return gson.fromJson(msg, mapType);
    }
}

