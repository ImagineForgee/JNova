package jnova.annotations.tcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to mark method parameters that should be populated from a JSON string.
 *
 * <p>This annotation is used in conjunction with a framework or library that can parse a JSON
 * string and populate the annotated parameter with the corresponding value.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonArg {
    /**
     * The key in the JSON string that corresponds to the parameter.
     *
     * @return the JSON key.
     */
    String value();
}
