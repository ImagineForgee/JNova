package jnova.tcp.metadata.introspection.dispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered type with its handler class and methods.
 *
 * This class stores the type name, the fully qualified name of the handler class,
 * and a list of registered methods associated with the type.
 */
public class RegisteredType {
    public String typeName;
    public String handlerClass;
    public List<RegisteredMethod> methods = new ArrayList<>();

        /**
     * Constructs a RegisteredType instance.
     *
     * @param typeName     The name of the type.
     * @param handlerClass The class that handles the type.  The fully qualified name of this
     *                     class will be stored.
     */
    public RegisteredType(String typeName, Class<?> handlerClass) {
        this.typeName = typeName;
        this.handlerClass = handlerClass.getName();
    }

        /**
     * Constructs a RegisteredType instance.
     *
     * @param typeName The name of the type.
     * @param handlerClass The class that handles this type.
     * @param methods The list of registered methods for this type.
     */
    public RegisteredType(String typeName, Class<?> handlerClass, List<RegisteredMethod> methods) {
        this.typeName = typeName;
        this.handlerClass = handlerClass.getName();
        this.methods = methods;
    }
}
