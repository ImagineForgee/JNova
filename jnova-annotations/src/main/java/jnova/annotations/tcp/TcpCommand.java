package jnova.annotations.tcp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Annotation used to mark methods as handlers for specific TCP commands.
 *  The {@link #value()} specifies the command string that the method should handle.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TcpCommand {
    /**
     * Returns the TCP command string that this method should handle.
     *
     * @return The TCP command string.
     */
    String value();
}
