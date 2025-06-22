package jnova.tcp.metadata.introspection;

import jnova.tcp.metadata.introspection.dispatcher.RegisteredArgumentResolver;
import jnova.tcp.metadata.introspection.dispatcher.RegisteredType;

import java.util.ArrayList;
import java.util.List;

public class DispatcherMetadata {
    public List<RegisteredType> types = new ArrayList<>();
    public List<RegisteredArgumentResolver> argumentResolvers = new ArrayList<>();
    public List<String> middleware = new ArrayList<>();
}
