package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.annotations.tcp.FromSession;
import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * Resolver for method arguments annotated with {@link FromSession}.
 *
 * <p>This resolver extracts data from the {@link TcpSession} associated with the current request.
 * It supports parameters annotated with {@link FromSession} and provides the session ID.
 */
public class FromSessionResolver implements ArgumentResolver {
        /**
     * Checks if the given parameter is annotated with {@link FromSession}.
     *
     * @param parameter The parameter to check.
     * @return {@code true} if the parameter is annotated with {@link FromSession}, {@code false} otherwise.
     */
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(FromSession.class);
    }
        /**
     * Resolves a parameter value based on the provided JSON data and TCP session.
     *
     * @param param The parameter to resolve.
     * @param json The JSON data map containing values.
     * @param session The TCP session associated with the request.
     * @param gson The Gson object for JSON serialization/deserialization.
     * @return The session ID as an object.
     */
    public Object resolve(Parameter param, Map<String, Object> json, TcpSession session, Gson gson) {
        return session.getId();
    }

        /**
     * Returns a list of annotations supported by this processor.
     *
     * @return A list containing the {@link FromSession} annotation class.
     */
    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(FromSession.class);
    }
}
