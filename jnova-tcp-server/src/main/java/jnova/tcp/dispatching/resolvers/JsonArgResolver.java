package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.annotations.tcp.JsonArg;
import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * An argument resolver that extracts and converts JSON arguments from a map.
 *
 * This class implements the {@link ArgumentResolver} interface to provide a way to
 * resolve method arguments annotated with {@link JsonArg} from a JSON map. It uses Gson
 * to convert the JSON value to the required parameter type.
 */
public class JsonArgResolver implements ArgumentResolver {
        /**
     * Checks if the given parameter is annotated with {@code JsonArg}.
     *
     * @param parameter The parameter to check.
     * @return {@code true} if the parameter has the {@code JsonArg} annotation, {@code false} otherwise.
     */
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(JsonArg.class);
    }
        /**
     * Resolves a parameter from a JSON map using Gson.
     *
     * @param param The parameter to resolve.
     * @param json The JSON map containing the data.
     * @param session The TCP session (unused in this method).
     * @param gson The Gson instance for JSON parsing.
     * @return The resolved parameter value, converted to the parameter's type.
     */
    public Object resolve(Parameter param, Map<String, Object> json, TcpSession session, Gson gson) {
        String key = param.getAnnotation(JsonArg.class).value();
        Object val = json.get(key);
        return gson.fromJson(gson.toJson(val), param.getType());
    }

        /**
     * Returns a list of supported annotation types.
     *
     * @return A list containing the {@link JsonArg} annotation class.
     */
    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(JsonArg.class);
    }
}
