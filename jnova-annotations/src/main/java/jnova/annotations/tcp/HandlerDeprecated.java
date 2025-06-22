package jnova.annotations.tcp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a handler is deprecated and should no longer be used.
 *
 * <p>This annotation provides information about when the handler was deprecated
 * and an optional message explaining why and suggesting alternatives.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerDeprecated {
    /**
     * Returns the version in which the handler was deprecated.
     *
     * @return the version string
     */
    String since();

    /**
     * Returns a message explaining why the handler was deprecated and suggesting
     * alternatives.
     *
     * @return the deprecation message, or an empty string if no message is provided
     */
    String message() default "";
}
