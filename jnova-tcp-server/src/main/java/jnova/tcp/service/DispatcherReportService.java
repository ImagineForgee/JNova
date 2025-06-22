package jnova.tcp.service;

import jnova.tcp.metadata.introspection.DispatcherMetadata;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredArgumentResolver;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredMethod;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredParameter;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredType;
import jnova.core.util.GsonFactory;
import com.google.gson.Gson;

import java.util.stream.Collectors;

/**
 * A service class that generates reports about the registered dispatchers,
 * argument resolvers, and middleware.
 *
 * <p>This class provides methods to register types, resolvers, and middleware,
 * and to generate reports in both raw JSON and human-readable formats.</p>
 */
public class DispatcherReportService {
    private static final DispatcherMetadata metadata = new DispatcherMetadata();
    private static final Gson gson = GsonFactory.get();

        /**
     * Retrieves the dispatcher metadata.
     *
     * @return The dispatcher metadata.
     */
    public static DispatcherMetadata getMetadata() {
        return metadata;
    }

        /**
         * Registers a new type with the metadata.
         *
         * @param type The RegisteredType to be added to the metadata.
         */
    public static void registerType(RegisteredType type) {
        metadata.types.add(type);
    }

        /**
     * Registers an argument resolver.
     *
     * @param resolver The argument resolver to register.
     */
    public static void registerResolver(RegisteredArgumentResolver resolver) {
        metadata.argumentResolvers.add(resolver);
    }

        /**
       * Registers a middleware class by adding its simple name to the middleware metadata.
       *
       * @param middlewareClass The class of the middleware to register.
       */
    public static void registerMiddleware(Class<?> middlewareClass) {
        metadata.middleware.add(middlewareClass.getSimpleName());
    }


    /**
     * Converts the metadata to a JSON string.
     *
     * @return a JSON string representation of the metadata.
     */
    public static String toJsonRawReport() {
        return gson.toJson(metadata);
    }

    /**
     * Generates a detailed report of registered types, handlers, argument resolvers, and middleware.
     *
     * The report includes information about registered types and their associated handlers,
     * methods with parameter details and return types, argument resolvers with supported annotations,
     * and configured middleware components.
     *
     * @return A formatted string containing the report details.
     */
    public static String toFancyReport() {
        StringBuilder sb = new StringBuilder();

        // TYPES & METHODS
        if (!metadata.types.isEmpty()) {
            sb.append("== Registered Types & Handlers ==\n");
            for (RegisteredType t : metadata.types) {
                String handler = simpleName(t.handlerClass);
                sb.append("- Type '" + t.typeName + "' -> Handler " + handler + "\n");

                for (RegisteredMethod m : t.methods) {
                    sb.append("    * Method: " + m.methodName + "(");
                    String params = m.parameters.stream()
                            .map(p -> simpleName(p.typeName)
                                    + (p.validationAnnotations.isEmpty() ? "" : "{" + String.join(",", p.validationAnnotations) + "}"))
                            .collect(Collectors.joining(", "));
                    sb.append(params).append(")");
                    sb.append(" -> ").append(simpleName(m.returnTypeName));

                    if (!m.declaredExceptions.isEmpty()) {
                        sb.append(" throws ")
                                .append(String.join(",", m.declaredExceptions));
                    }
                    if (m.deprecated) {
                        sb.append(" [DEPRECATED since ")
                                .append(m.sinceVersion)
                                .append(": ")
                                .append(m.deprecationMessage)
                                .append("]");
                    } else if (m.sinceVersion != null) {
                        sb.append(" [since ").append(m.sinceVersion).append("]");
                    }

                    sb.append("\n");
                }
            }
        }

        // ARGUMENT RESOLVERS
        if (!metadata.argumentResolvers.isEmpty()) {
            sb.append("== Argument Resolvers ==\n");
            for (RegisteredArgumentResolver r : metadata.argumentResolvers) {
                String name = simpleName(r.resolverClass);
                String annos = r.supportedAnnotations.stream()
                        .map(a -> simpleName(a))
                        .collect(Collectors.joining(","));
                sb.append("- " + name + (annos.isEmpty() ? "" : " [@" + annos + "]")).append("\n");
            }
        }

        // MIDDLEWARE
        if (!metadata.middleware.isEmpty()) {
            sb.append("== Middleware ==\n");
            for (String mw : metadata.middleware) {
                sb.append("- " + mw + "\n");
            }
        }

        return sb.toString();
    }

        /**
     * Extracts the simple name from a fully qualified class name.
     *
     * @param fqcn The fully qualified class name.
     * @return The simple name of the class, or the original string if no package is found.
     */
    private static String simpleName(String fqcn) {
        int idx = fqcn.lastIndexOf('.');
        return idx >= 0 ? fqcn.substring(idx + 1) : fqcn;
    }
}
