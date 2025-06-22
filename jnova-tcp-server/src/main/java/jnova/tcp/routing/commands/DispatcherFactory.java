package jnova.tcp.routing.commands;

import jnova.tcp.dispatching.DispatcherContext;
import jnova.tcp.dispatching.TcpMiddleware;
import jnova.tcp.dispatching.resolvers.ArgumentResolver;
import jnova.tcp.handler.CommandRequestHandler;
import jnova.tcp.handler.HandlerEnum;
import jnova.tcp.handler.JsonStructureRequestHandler;
import jnova.tcp.handler.TcpRequestHandler;

import java.util.List;

/**
 * A factory class for creating {@link TcpRequestHandler} instances.
 *
 * <p>This class provides a builder pattern for configuring and creating different types of
 * `TcpRequestHandler` based on specified parameters such as base package, argument resolvers,
 * middleware, and handler type. It supports creating handlers for JSON structures and command-based
 * requests.</p>
 */
public class DispatcherFactory {

        /**
     * Returns a new Builder for constructing objects of this type.
     *
     * @return A new Builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

        /**
     * A builder class for constructing {@link TcpRequestHandler} instances.
     *
     * <p>This class provides a fluent interface for configuring the properties of a {@link
     * TcpRequestHandler}, such as the base package for scanning handlers, argument resolvers, TCP
     * middleware, and the handler type. It supports building different types of {@link
     * TcpRequestHandler} based on the specified handler type (JSON or COMMAND).
     */
    public static class Builder {
        private String basePackage;
        private List<ArgumentResolver> resolvers;
        private List<TcpMiddleware> middleware;
        private HandlerEnum handlerType;

                /**
         * Sets the base package for generated classes.
         *
         * @param basePackage The base package name.
         * @return This {@code Builder} instance for chaining.
         */
        public Builder basePackage(String basePackage) {
            this.basePackage = basePackage;
            return this;
        }

                /**
         * Sets the list of argument resolvers to use.
         *
         * @param resolvers The list of {@link ArgumentResolver} to use.
         * @return This {@link Builder} instance for chaining.
         */
        public Builder resolvers(List<ArgumentResolver> resolvers) {
            this.resolvers = resolvers;
            return this;
        }

                /**
         * Sets the list of TCP middleware to be used by the server.
         *
         * @param middleware The list of {@link TcpMiddleware} to be applied.
         * @return This {@link Builder} instance for chaining.
         */
        public Builder middleware(List<TcpMiddleware> middleware) {
            this.middleware = middleware;
            return this;
        }

                /**
         * Sets the handler type for the builder.
         *
         * @param handlerType The handler type to set.
         * @return This builder instance.
         */
        public Builder handlerType(HandlerEnum handlerType) {
            this.handlerType = handlerType;
            return this;
        }

                /**
         * Builds a {@link TcpRequestHandler} based on the configured handler type.
         *
         * @return A {@link TcpRequestHandler} instance.
         * @throws IllegalStateException if the basePackage or handlerType is not provided.
         * @throws IllegalArgumentException if an unknown handler type is specified.
         */
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
