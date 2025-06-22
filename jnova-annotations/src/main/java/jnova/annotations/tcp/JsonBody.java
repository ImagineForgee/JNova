package jnova.annotations.tcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method parameter should be populated from the JSON body of the request.
 * <p>
 * This annotation is used to mark parameters in a controller method that should be bound
 * to the JSON payload of the incoming HTTP request.  It allows for easy deserialization
 * of JSON data into method parameters.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBody {}
