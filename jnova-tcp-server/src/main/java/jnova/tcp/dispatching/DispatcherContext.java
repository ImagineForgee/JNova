package jnova.tcp.dispatching;

import com.google.gson.Gson;
import jnova.core.validation.ParameterValidator;
import jnova.tcp.dispatching.resolvers.*;

import java.util.List;

public class DispatcherContext {
    public final ArgumentResolverRegistry resolverRegistry;
    public final ParameterValidator validator;
    public final TcpTypeHandlerRegistry typeRegistry;
    public final TcpSubHandlerDispatcher subDispatcher;
    public final List<TcpMiddleware> middleware;

    public DispatcherContext(String basePackage, List<ArgumentResolver> resolvers, List<TcpMiddleware> middleware) {
        this.resolverRegistry = new ArgumentResolverRegistry();
        List<ArgumentResolver> defaultResolvers = List.of(new TcpSessionResolver(), new JsonArgResolver(), new FromSessionResolver(), new JsonBodyResolver());
        this.resolverRegistry.registerAll(defaultResolvers);
        this.resolverRegistry.registerAll(resolvers);

        this.validator = new ParameterValidator();

        this.typeRegistry = new TcpTypeHandlerRegistry(resolverRegistry);
        this.typeRegistry.scanAndRegister(basePackage);

        if (middleware == null || middleware.isEmpty()) {
            this.middleware = List.of(new TcpMiddleware() {});
        } else {
            this.middleware = middleware;
        }

        this.subDispatcher = new TcpSubHandlerDispatcher(typeRegistry, validator, this.middleware);


    }
}
