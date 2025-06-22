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

/**
 *  Registry for managing TCP type handlers.
 *
 *  This class is responsible for scanning, registering, and providing access to handler methods
 *  associated with specific TCP types. It utilizes annotations like {@link TcpType}, {@link JsonProperty},
 *  {@link JsonArg}, and {@link JsonBody} to identify and configure handler methods. It also provides
 *  argument resolution capabilities using {@link ArgumentResolverRegistry}.
 */
public class TcpTypeHandler {
    private final Map<String, List<HandlerMethod>> registry = new ConcurrentHashMap<>();
    private final ArgumentResolverRegistry resolverRegistry;

        /**
     * Constructs a TcpTypeHandlerRegistry with the given ArgumentResolverRegistry.
     *
     * @param resolverRegistry The ArgumentResolverRegistry to use for resolving arguments.
     */
    public TcpTypeHandler(ArgumentResolverRegistry resolverRegistry) {
        this.resolverRegistry = resolverRegistry;
    }

        /**
     * Scans the specified base package for classes annotated with {@link TcpType} and registers their methods
     * annotated with {@link JsonProperty} as TCP handlers.
     *
     * <p>This method uses ClassGraph to scan the classpath for classes within the given base package
     * that are annotated with {@link TcpType}. For each such class, it instantiates the class and
     * iterates through its methods, registering those annotated with {@link JsonProperty} as handlers.
     * The handler registration involves extracting information about the method's parameters, including
     * their types, annotations (e.g., {@link JsonArg}, {@link JsonBody}, {@link FromSession}), and
     * validation constraints. This information is then used to create {@link HandlerMethod} instances,
     * which are stored in the {@link registry} for later dispatching of TCP requests.
     *
     * <p>Additionally, the method collects metadata about each registered handler method, such as its
     * name, parameter types, return type, declared exceptions, and deprecation status (if applicable).
     * This metadata is encapsulated in {@link RegisteredMethod} instances and associated with a
     * {@link RegisteredType} instance, which is then registered with the
     * {@link DispatcherReportService} for documentation and reporting purposes.
     *
     * @param basePackage The base package to scan for TCP handler classes.
     * @throws RuntimeException If an error occurs during the scanning or registration process.
     */
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

        /**
     * Checks if a parameter has any validation annotations from javax.validation or org.hibernate.validator.
     *
     * @param p The parameter to check.
     * @return True if the parameter has validation annotations, false otherwise.
     */
    private static boolean hasValidationAnnotations(Parameter p) {
        return Arrays.stream(p.getAnnotations())
                .anyMatch(a -> {
                    String pkg = a.annotationType().getPackageName();
                    return pkg.startsWith("javax.validation") || pkg.startsWith("org.hibernate.validator");
                });
    }

        /**
     * Finds a handler method based on the given type, key, and value.
     *
     * @param type  The type of the handler.
     * @param key   The key to match.
     * @param value The value to match.
     * @return An Optional containing the handler method if found, otherwise empty.
     */
    public Optional<HandlerMethod> findHandler(String type, String key, String value) {
        return registry.getOrDefault(type, List.of()).stream()
                .filter(h -> h.key().equalsIgnoreCase(key) && h.value().equalsIgnoreCase(value))
                .findFirst();
    }

        /**
     * Resolves the arguments for a given method using a resolver registry.
     *
     * @param method The method whose arguments need to be resolved.
     * @param json A map containing the JSON data used for resolving arguments.
     * @param session The TcpSession associated with the request.
     * @return An array of objects representing the resolved arguments for the method.
     */
    public Object[] resolveArguments(Method method, Map<String, Object> json, TcpSession session) {
        return resolverRegistry.resolveArguments(method.getParameters(), json, session);
    }

    public record HandlerMethod(String key, String value, Method method, Object instance) {}
}
