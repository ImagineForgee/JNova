package jnova.tcp.metadata.introspection.dispatcher;

import java.util.ArrayList;
import java.util.List;

public class RegisteredParameter {
    public String name;
    public String typeName;
    public String sourceAnnotation;
    public boolean validated;
    public List<String> validationAnnotations = new ArrayList<>();

    public RegisteredParameter(String name, Class<?> type, String sourceAnnotation, boolean validated) {
        this.name = name;
        this.typeName = type.getName();
        this.sourceAnnotation = sourceAnnotation;
        this.validated = validated;
    }
}

