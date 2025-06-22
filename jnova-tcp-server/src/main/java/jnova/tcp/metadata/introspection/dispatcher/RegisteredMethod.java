package jnova.tcp.metadata.introspection.dispatcher;

import java.util.ArrayList;
import java.util.List;

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

    public RegisteredMethod(String methodName, String key, String value) {
        this.methodName = methodName;
        this.key = key;
        this.value = value;
    }

    public RegisteredMethod(String methodName, String key, String value, List<RegisteredParameter> parameters) {
        this.methodName = methodName;
        this.key = key;
        this.value = value;
        this.parameters = parameters;
    }
}
