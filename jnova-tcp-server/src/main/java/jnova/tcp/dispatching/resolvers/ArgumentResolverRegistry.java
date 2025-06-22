package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.core.util.GsonFactory;
import jnova.tcp.TcpSession;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredArgumentResolver;
import jnova.tcp.service.DispatcherReportService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Registry for managing and resolving method argument resolvers.
 *
 * This class maintains a list of {@link ArgumentResolver} instances and provides
 * methods for registering resolvers and resolving arguments for method parameters
 * based on the registered resolvers. It also interacts with a
 * {@link DispatcherReportService} to register the resolvers for reporting purposes.
 */
public class ArgumentResolverRegistry {
    private final List<ArgumentResolver> resolvers = new ArrayList<>();
    private final Set<String> registeredClasses = new HashSet<>();
    private final Gson gson = GsonFactory.get();

        /**
         * Registers an argument resolver.
         *
         * <p>This method adds the given {@link ArgumentResolver} to the list of registered resolvers.
         * It also registers the resolver with the {@link DispatcherReportService}.
         *
         * @param resolver The argument resolver to register.
         */
    public void register(ArgumentResolver resolver) {
        String className = resolver.getClass().getName();
        if (!registeredClasses.add(className)) {
            return;
        }

        resolvers.add(resolver);

        List<String> annotations = new ArrayList<>();
        for (Class<? extends Annotation> anno : resolver.supportedAnnotations()) {
            annotations.add(anno.getName());
        }

        RegisteredArgumentResolver registered =
                new RegisteredArgumentResolver(className, annotations);

        DispatcherReportService.registerResolver(registered);
    }


        /**
     * Registers all provided argument resolvers.
     *
     * @param resolverList A list of ArgumentResolver instances to register.
     */
    public void registerAll(List<ArgumentResolver> resolverList) {
        for (ArgumentResolver resolver : resolverList) {
            register(resolver);
        }
    }

        /**
     * Resolves the arguments for a method based on the provided parameters, JSON data, and TCP session.
     *
     * It iterates through the parameters and attempts to resolve each one using a suitable ArgumentResolver.
     *
     * @param parameters An array of Parameter objects representing the method's parameters.
     * @param json A Map containing JSON data used for resolving arguments.
     * @param session The TcpSession associated with the request.
     * @return An array of Objects representing the resolved arguments, in the same order as the input parameters.
     */
    public Object[] resolveArguments(Parameter[] parameters, Map<String, Object> json, TcpSession session) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            for (ArgumentResolver resolver : resolvers) {
                if (resolver.supports(parameters[i])) {
                    args[i] = resolver.resolve(parameters[i], json, session, gson);
                    break;
                }
            }
        }
        return args;
    }
}
