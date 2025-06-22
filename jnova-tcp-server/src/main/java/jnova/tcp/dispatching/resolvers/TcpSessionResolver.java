package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.tcp.TcpSession;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * An argument resolver for injecting {@link TcpSession} instances into handler methods.
 *
 * <p>This resolver checks if a parameter is of type {@link TcpSession} and, if so,
 * resolves it to the current session.
 */
public class TcpSessionResolver implements ArgumentResolver {
        /**
     * Checks if the given parameter is supported by this component.
     *
     * @param parameter The parameter to check.
     * @return {@code true} if the parameter type is TcpSession.class, {@code false} otherwise.
     */
    @Override public boolean supports(Parameter parameter) {
        return parameter.getType().equals(TcpSession.class);
    }
        /**
     * Resolves a parameter to the current TCP session.
     *
     * @param parameter The parameter to resolve.
     * @param json The JSON data associated with the request.
     * @param session The current TCP session.
     * @param gson The Gson instance for JSON serialization/deserialization.
     * @return The TCP session.
     */
    @Override public Object resolve(Parameter parameter, Map<String, Object> json, TcpSession session, Gson gson) {
        return session;
    }
}
