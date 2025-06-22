package jnova.tcp.routing.commands;

import jnova.tcp.dispatching.DispatcherContext;
import jnova.tcp.dispatching.TcpMiddleware;
import jnova.tcp.dispatching.resolvers.ArgumentResolver;
import jnova.tcp.handler.CommandRequestHandler;
import jnova.tcp.handler.HandlerEnum;
import jnova.tcp.handler.JsonStructureRequestHandler;
import jnova.tcp.handler.TcpRequestHandler;

import java.util.List;

public class DispatcherFactory {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String basePackage;
        private List<ArgumentResolver> resolvers;
        private List<TcpMiddleware> middleware;
        private HandlerEnum handlerType;

        public Builder basePackage(String basePackage) {
            this.basePackage = basePackage;
            return this;
        }

        public Builder resolvers(List<ArgumentResolver> resolvers) {
            this.resolvers = resolvers;
            return this;
        }

        public Builder middleware(List<TcpMiddleware> middleware) {
            this.middleware = middleware;
            return this;
        }

        public Builder handlerType(HandlerEnum handlerType) {
            this.handlerType = handlerType;
            return this;
        }

        public TcpRequestHandler build() {
            if (basePackage == null || handlerType == null) {
                throw new IllegalStateException("basePackage and handlerType must be provided");
            }
            switch (handlerType) {
                case JSON -> {
                    DispatcherContext context = new DispatcherContext(
                            basePackage,
                            resolvers,
                            middleware
                    );
                    return new JsonStructureRequestHandler(context);
                }
                case COMMAND -> {
                    TcpCommandRegistry registry = new TcpCommandRegistry();
                    TcpCommandScanner.scanAndRegister(basePackage, registry);
                    TcpCommandDispatcher dispatcher = new TcpCommandDispatcher(registry);
                    return new CommandRequestHandler(dispatcher);
                }
                default -> throw new IllegalArgumentException("Unknown handler type: " + handlerType);
            }
        }
    }
}
