package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.annotations.tcp.JsonArg;
import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class JsonArgResolver implements ArgumentResolver {
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(JsonArg.class);
    }
    public Object resolve(Parameter param, Map<String, Object> json, TcpSession session, Gson gson) {
        String key = param.getAnnotation(JsonArg.class).value();
        Object val = json.get(key);
        return gson.fromJson(gson.toJson(val), param.getType());
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(JsonArg.class);
    }
}
