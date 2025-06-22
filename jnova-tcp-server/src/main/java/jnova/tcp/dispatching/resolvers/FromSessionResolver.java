package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.annotations.tcp.FromSession;
import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class FromSessionResolver implements ArgumentResolver {
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(FromSession.class);
    }
    public Object resolve(Parameter param, Map<String, Object> json, TcpSession session, Gson gson) {
        return session.getId();
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(FromSession.class);
    }
}
