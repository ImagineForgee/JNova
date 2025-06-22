package jnova.tcp.metadata.introspection.dispatcher;

import java.util.ArrayList;
import java.util.List;

public class RegisteredArgumentResolver {
    public String resolverClass;
    public List<String> supportedAnnotations = new ArrayList<>();

    public RegisteredArgumentResolver(String resolverClass) {
        this.resolverClass = resolverClass;
    }

    public RegisteredArgumentResolver(String resolverClass, List<String> supportedAnnotations) {
        this.resolverClass = resolverClass;
        this.supportedAnnotations = supportedAnnotations;
    }
}
