package jnova.tcp.metadata.introspection.dispatcher;

import java.util.ArrayList;
import java.util.List;

public class RegisteredType {
    public String typeName;
    public String handlerClass;
    public List<RegisteredMethod> methods = new ArrayList<>();

    public RegisteredType(String typeName, Class<?> handlerClass) {
        this.typeName = typeName;
        this.handlerClass = handlerClass.getName();
    }

    public RegisteredType(String typeName, Class<?> handlerClass, List<RegisteredMethod> methods) {
        this.typeName = typeName;
        this.handlerClass = handlerClass.getName();
        this.methods = methods;
    }
}
