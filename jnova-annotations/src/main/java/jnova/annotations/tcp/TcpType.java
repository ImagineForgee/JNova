package jnova.annotations.tcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as a specific TCP type.
 *
 * <p>This annotation is used to associate a class with a specific TCP type,
 * which can be useful for routing or handling different types of TCP messages.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TcpType {

    /**
     * The value of the TCP type.  This should be a unique identifier
     * for this particular TCP type.
     *
     * @return the TCP type value as a String.  Cannot be null or empty.
     */
    String value();
}
