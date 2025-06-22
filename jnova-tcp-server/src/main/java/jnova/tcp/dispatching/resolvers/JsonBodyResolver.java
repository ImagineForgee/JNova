package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.annotations.tcp.JsonBody;
import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class JsonBodyResolver implements ArgumentResolver {
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(JsonBody.class);
    }
    public Object resolve(Parameter param, Map<String, Object> json, TcpSession session, Gson gson) {
        return gson.fromJson(gson.toJson(json), param.getType());
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(JsonBody.class);
    }
}
