package jnova.core.exceptions;

/**
 * Exception thrown when an error occurs during argument resolution.
 *
 * <p>This exception indicates that there was a problem processing or interpreting
 * command-line arguments. It typically wraps a more specific error message
 * describing the cause of the resolution failure.
 */
public class ArgumentResolutionException extends RuntimeException {
        /**
     * Constructs a new ArgumentResolutionException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public ArgumentResolutionException(String message) {
        super(message);
    }
}
