package jnova.tcp.dispatching;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import jnova.annotations.tcp.*;
import jnova.tcp.TcpSession;
import jnova.tcp.dispatching.resolvers.ArgumentResolverRegistry;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredMethod;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredParameter;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredType;
import jnova.tcp.service.DispatcherReportService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TcpTypeHandlerRegistry {
    private final Map<String, List<HandlerMethod>> registry = new ConcurrentHashMap<>();
    private final ArgumentResolverRegistry resolverRegistry;

    public TcpTypeHandlerRegistry(ArgumentResolverRegistry resolverRegistry) {
        this.resolverRegistry = resolverRegistry;
    }

    public void scanAndRegister(String basePackage) {
        try (ScanResult result = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(basePackage)
                .scan()) {

            for (ClassInfo classInfo : result.getClassesWithAnnotation(TcpType.class.getName())) {
                Class<?> clazz = classInfo.loadClass();
                TcpType tcpType = clazz.getAnnotation(TcpType.class);
                Object instance = clazz.getDeclaredConstructor().newInstance();

                List<RegisteredMethod> registeredMethods = new ArrayList<>();

                for (Method method : clazz.getDeclaredMethods()) {
                    if (!method.isAnnotationPresent(JsonProperty.class)) continue;

                    JsonProperty jp = method.getAnnotation(JsonProperty.class);
                    registry
                            .computeIfAbsent(tcpType.value(), k -> new ArrayList<>())
                            .add(new HandlerMethod(jp.key(), jp.value(), method, instance));

                    List<RegisteredParameter> parameters = new ArrayList<>();
                    for (Parameter param : method.getParameters()) {
                        String name = param.getName();
                        String annotation = null;
                        if (param.isAnnotationPresent(JsonArg.class)) {
                            annotation = param.getAnnotation(JsonArg.class).value();
                        } else if (param.isAnnotationPresent(JsonBody.class)) {
                            annotation = "@JsonBody";
                        } else if (param.isAnnotationPresent(FromSession.class)) {
                            annotation = "@FromSession";
                        }

                        RegisteredParameter rp = new RegisteredParameter(
                                name,
                                param.getType(),
                                annotation,
                                hasValidationAnnotations(param)
                        );
                        for (Annotation a : param.getAnnotations()) {
                            String pkg = a.annotationType().getPackageName();
                            if (pkg.startsWith("javax.validation") || pkg.startsWith("org.hibernate.validator") || pkg.startsWith("jakarta.validation")) {
                                rp.validationAnnotations.add(a.annotationType().getSimpleName());
                            }
                        }
                        parameters.add(rp);
                    }

                    RegisteredMethod rm = new RegisteredMethod(
                            method.getName(),
                            jp.key(),
                            jp.value(),
                            parameters
                    );
                    rm.returnTypeName = method.getGenericReturnType().getTypeName();

                    for (Class<?> ex : method.getExceptionTypes()) {
                        rm.declaredExceptions.add(ex.getSimpleName());
                    }

                    if (method.isAnnotationPresent(HandlerDeprecated.class)) {
                        HandlerDeprecated hd = method.getAnnotation(HandlerDeprecated.class);
                        rm.deprecated = true;
                        rm.sinceVersion = hd.since();
                        rm.deprecationMessage = hd.message();
                    }

                    registeredMethods.add(rm);
                }

                RegisteredType registeredType = new RegisteredType(
                        tcpType.value(),
                        clazz,
                        registeredMethods
                );
                DispatcherReportService.registerType(registeredType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan and register TCP handlers", e);
        }
    }

    private static boolean hasValidationAnnotations(Parameter p) {
        return Arrays.stream(p.getAnnotations())
                .anyMatch(a -> {
                    String pkg = a.annotationType().getPackageName();
                    return pkg.startsWith("javax.validation") || pkg.startsWith("org.hibernate.validator");
                });
    }

    public Optional<HandlerMethod> findHandler(String type, String key, String value) {
        return registry.getOrDefault(type, List.of()).stream()
                .filter(h -> h.key().equalsIgnoreCase(key) && h.value().equalsIgnoreCase(value))
                .findFirst();
    }

    public Object[] resolveArguments(Method method, Map<String, Object> json, TcpSession session) {
        return resolverRegistry.resolveArguments(method.getParameters(), json, session);
    }

    public record HandlerMethod(String key, String value, Method method, Object instance) {}
}
