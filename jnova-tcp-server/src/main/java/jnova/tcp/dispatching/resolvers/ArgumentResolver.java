package jnova.tcp.dispatching.resolvers;

import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public interface ArgumentResolver {
    boolean supports(Parameter parameter);
    Object resolve(Parameter parameter, Map<String, Object> json, TcpSession session, com.google.gson.Gson gson);

    default List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of();
    }
}
