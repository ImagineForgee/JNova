package jnova.core.exceptions;

/**
 * An exception that is thrown when an invocation fails.
 *
 * This exception is a subclass of {@link RuntimeException} and is used to
 * indicate that an error occurred during the invocation of a method or function.
 */
public class InvocationException extends RuntimeException {
        /**
     * Constructs an {@code InvocationException} with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     */
    public InvocationException(String message) {
        super(message);
    }
}
