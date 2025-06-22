package jnova.tcp.service;

import jnova.tcp.metadata.introspection.DispatcherMetadata;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredArgumentResolver;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredMethod;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredParameter;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredType;
import jnova.core.util.GsonFactory;
import com.google.gson.Gson;

import java.util.stream.Collectors;

public class DispatcherReportService {
    private static final DispatcherMetadata metadata = new DispatcherMetadata();
    private static final Gson gson = GsonFactory.get();

    public static DispatcherMetadata getMetadata() {
        return metadata;
    }

    public static void registerType(RegisteredType type) {
        metadata.types.add(type);
    }

    public static void registerResolver(RegisteredArgumentResolver resolver) {
        metadata.argumentResolvers.add(resolver);
    }

    public static void registerMiddleware(Class<?> middlewareClass) {
        metadata.middleware.add(middlewareClass.getSimpleName());
    }

    /**
     * Output raw JSON report.
     */
    public static String toJsonRawReport() {
        return gson.toJson(metadata);
    }

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

    private static String simpleName(String fqcn) {
        int idx = fqcn.lastIndexOf('.');
        return idx >= 0 ? fqcn.substring(idx + 1) : fqcn;
    }
}
