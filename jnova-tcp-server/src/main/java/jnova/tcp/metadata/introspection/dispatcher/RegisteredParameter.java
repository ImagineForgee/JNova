package jnova.tcp.metadata.introspection.dispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered parameter with its metadata.
 *
 * This class stores information about a parameter, including its name, type, source annotation,
 * validation status, and any associated validation annotations.
 */
public class RegisteredParameter {
    public String name;
    public String typeName;
    public String sourceAnnotation;
    public boolean validated;
    public List<String> validationAnnotations = new ArrayList<>();

        /**
     * Constructs a RegisteredParameter object.
     *
     * @param name The name of the parameter.
     * @param type The class type of the parameter.
     * @param sourceAnnotation The annotation from which the parameter was sourced.
     * @param validated A boolean indicating whether the parameter has been validated.
     */
    public RegisteredParameter(String name, Class<?> type, String sourceAnnotation, boolean validated) {
        this.name = name;
        this.typeName = type.getName();
        this.sourceAnnotation = sourceAnnotation;
        this.validated = validated;
    }
}

