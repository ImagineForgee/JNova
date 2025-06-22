package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.annotations.tcp.JsonBody;
import jnova.tcp.TcpSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * Resolves arguments annotated with {@link JsonBody} from a JSON map.
 *
 * This class implements the {@link ArgumentResolver} interface to provide a way to
 * automatically deserialize JSON data into method parameters. It uses Gson for
 * JSON serialization and deserialization.
 */
public class JsonBodyResolver implements ArgumentResolver {
        /**
     * Checks if the given parameter is annotated with {@link JsonBody}.
     *
     * @param parameter The parameter to check.
     * @return {@code true} if the parameter is annotated with {@link JsonBody}, {@code false} otherwise.
     */
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(JsonBody.class);
    }
        /**
     * Resolves a parameter by converting a JSON map to the parameter's type using Gson.
     *
     * @param param The parameter to resolve.
     * @param json  The JSON map containing the data for the parameter.
     * @param session The TcpSession associated with the request.
     * @param gson  The Gson instance used for JSON conversion.
     * @return An object of the parameter's type, populated with data from the JSON map.
     */
    public Object resolve(Parameter param, Map<String, Object> json, TcpSession session, Gson gson) {
        return gson.fromJson(gson.toJson(json), param.getType());
    }

        /**
     * Returns a list of supported annotation types.
     *
     * <p>This implementation returns a list containing only {@link JsonBody}.
     *
     * @return A list of {@link Class} objects representing the supported annotation types.
     */
    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(JsonBody.class);
    }
}
