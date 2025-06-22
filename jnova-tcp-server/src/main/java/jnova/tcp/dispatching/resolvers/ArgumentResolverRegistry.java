package jnova.tcp.dispatching.resolvers;

import com.google.gson.Gson;
import jnova.core.util.GsonFactory;
import jnova.tcp.TcpSession;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredArgumentResolver;
import jnova.tcp.service.DispatcherReportService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;

public class ArgumentResolverRegistry {
    private final List<ArgumentResolver> resolvers = new ArrayList<>();
    private final Set<String> registeredClasses = new HashSet<>();
    private final Gson gson = GsonFactory.get();

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


    public void registerAll(List<ArgumentResolver> resolverList) {
        for (ArgumentResolver resolver : resolverList) {
            register(resolver);
        }
    }

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
