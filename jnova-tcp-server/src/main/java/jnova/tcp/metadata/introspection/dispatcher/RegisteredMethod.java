package jnova.tcp.metadata.introspection.dispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered method with its associated metadata.
 *
 * This class encapsulates information about a method, including its name, key, value,
 * return type, required roles, parameters, declared exceptions, versioning details,
 * and deprecation status.
 */
public class RegisteredMethod {
    public String methodName;
    public String key;
    public String value;
    public String returnTypeName;
    public List<String> requiredRoles = new ArrayList<>();
    public List<RegisteredParameter> parameters = new ArrayList<>();

    public List<String> declaredExceptions = new ArrayList<>();
    public String sinceVersion = null;
    public boolean deprecated = false;
    public String deprecationMessage = null;

        /**
     * Constructs a RegisteredMethod object.
     *
     * @param methodName The name of the method.
     * @param key        The key associated with the method.
     * @param value      The value associated with the method.
     */
    public RegisteredMethod(String methodName, String key, String value) {
        this.methodName = methodName;
        this.key = key;
        this.value = value;
    }

        /**
     * Constructs a new RegisteredMethod instance.
     *
     * @param methodName The name of the method.
     * @param key The key associated with the method.
     * @param value The value associated with the method.
     * @param parameters The list of registered parameters for the method.
     */
    public RegisteredMethod(String methodName, String key, String value, List<RegisteredParameter> parameters) {
        this.methodName = methodName;
        this.key = key;
        this.value = value;
        this.parameters = parameters;
    }
}
