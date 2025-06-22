package jnova.tcp.dispatching;

import jnova.core.validation.ParameterValidator;
import jnova.tcp.dispatching.resolvers.*;

import java.util.List;

/**
 * Context class that holds registries, validator, type handler, sub-dispatcher,
 * and middleware used for processing dispatched TCP requests.
 *
 * <p>This context manages argument resolution, parameter validation, TCP type handling,
 * sub-handler dispatching, and middleware execution for incoming TCP requests.
 */
public class DispatcherContext {
    public final ArgumentResolverRegistry resolverRegistry;
    public final ParameterValidator validator;
    public final TcpTypeHandler typeRegistry;
    public final TcpSubHandlerDispatcher subDispatcher;
    public final List<TcpMiddleware> middleware;

        /**
     * Constructs a DispatcherContext.
     *
     * @param basePackage The base package to scan for type handlers.
     * @param resolvers A list of argument resolvers to register.
     * @param middleware A list of TCP middleware to apply. If null or empty, defaults to a no-op middleware.
     */
    public DispatcherContext(String basePackage, List<ArgumentResolver> resolvers, List<TcpMiddleware> middleware) {
        this.resolverRegistry = new ArgumentResolverRegistry();
        List<ArgumentResolver> defaultResolvers = List.of(new TcpSessionResolver(), new JsonArgResolver(), new FromSessionResolver(), new JsonBodyResolver());
        this.resolverRegistry.registerAll(defaultResolvers);
        this.resolverRegistry.registerAll(resolvers);

        this.validator = new ParameterValidator();

        this.typeRegistry = new TcpTypeHandler(resolverRegistry);
        this.typeRegistry.scanAndRegister(basePackage);

        if (middleware == null || middleware.isEmpty()) {
            this.middleware = List.of(new TcpMiddleware() {});
        } else {
            this.middleware = middleware;
        }

        this.subDispatcher = new TcpSubHandlerDispatcher(typeRegistry, validator, this.middleware);


    }
}
