package jnova.tcp.metadata.introspection.dispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 *  Represents a registered argument resolver with its class name and supported annotations.
 *
 *  This class holds information about an argument resolver, including the fully qualified name
 *  of the resolver class and a list of annotations that the resolver supports.
 */
public class RegisteredArgumentResolver {
    public String resolverClass;
    public List<String> supportedAnnotations = new ArrayList<>();

        /**
     * Constructs a RegisteredArgumentResolver with the specified resolver class name.
     *
     * @param resolverClass The fully qualified name of the argument resolver class.
     */
    public RegisteredArgumentResolver(String resolverClass) {
        this.resolverClass = resolverClass;
    }

        /**
     * Constructs a RegisteredArgumentResolver with the specified resolver class and supported annotations.
     *
     * @param resolverClass The fully qualified name of the argument resolver class.
     * @param supportedAnnotations A list of fully qualified names of annotations supported by the resolver.
     */
    public RegisteredArgumentResolver(String resolverClass, List<String> supportedAnnotations) {
        this.resolverClass = resolverClass;
        this.supportedAnnotations = supportedAnnotations;
    }
}
