package jnova.annotations.tcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates a parameter should be populated from the user's session.
 * <p>
 * This annotation is used to mark parameters in methods that should be automatically
 * populated with data stored in the user's session.  The name of the session attribute
 * to use is derived from the parameter name.
 * </p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FromSession {}
