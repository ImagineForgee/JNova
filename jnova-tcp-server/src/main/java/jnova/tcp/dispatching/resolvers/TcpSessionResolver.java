package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.annotations.tcp.JsonBody;
import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class TcpSessionResolver implements ArgumentResolver {
    @Override public boolean supports(Parameter parameter) {
        return parameter.getType().equals(TcpSession.class);
    }
    @Override public Object resolve(Parameter parameter, Map<String, Object> json, TcpSession session, Gson gson) {
        return session;
    }
}
