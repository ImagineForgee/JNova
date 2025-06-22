package jnova.annotations.tcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Annotation used to map a method to a JSON property.
 *  This annotation is used to define the key and value of a JSON property
 *  that corresponds to the annotated method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonProperty {
    /**
     *  Returns the key of the JSON property.
     *
     *  @return the key of the JSON property
     */
    String key();

    /**
     *  Returns the value of the JSON property.
     *
     *  @return the value of the JSON property
     */
    String value();
}
