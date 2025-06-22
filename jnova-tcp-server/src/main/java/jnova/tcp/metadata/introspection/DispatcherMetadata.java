package jnova.tcp.metadata.introspection;

import jnova.tcp.metadata.introspection.dispatcher.RegisteredArgumentResolver;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredType;

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata class that holds information about registered types, argument resolvers, and middleware.
 * <p>
 * This class is used to store and manage the registered components within a dispatcher.  It includes
 * lists for types, argument resolvers, and middleware names, enabling easy access and management of
 * the dispatcher's configuration.
 */
public class DispatcherMetadata {
    public List<RegisteredType> types = new ArrayList<>();
    public List<RegisteredArgumentResolver> argumentResolvers = new ArrayList<>();
    public List<String> middleware = new ArrayList<>();
}
