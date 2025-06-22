package jnova.tcp.dispatching.resolvers;

import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * An interface for resolving method argument values from a request.
 *
 * Implementations of this interface are responsible for determining if they
 * support a given parameter and for resolving the argument value from the
 * request data.
 */
public interface ArgumentResolver {
        /**
     * Checks if this object supports the given parameter.
     *
     * @param parameter The parameter to check for support.
     * @return `true` if the parameter is supported, `false` otherwise.
     */
    boolean supports(Parameter parameter);
        /**
     * Resolves a parameter value from a JSON map.
     *
     * @param parameter The parameter to resolve.
     * @param json The JSON map containing data.
     * @param session The TCP session associated with the request.
     * @param gson The Gson instance for JSON serialization/deserialization.
     * @return The resolved object, or null if the parameter cannot be resolved.
     */
    Object resolve(Parameter parameter, Map<String, Object> json, TcpSession session, com.google.gson.Gson gson);

        /**
         * Returns a list of annotation types supported by this processor.
         *
         * <p>The default implementation returns an empty list. Subclasses can override this
         * method to specify the annotation types they support.
         *
         * @return A list of {@link Class} objects representing the supported annotation types.
         */
    default List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of();
    }
}
