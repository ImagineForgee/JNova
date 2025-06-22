package jnova.core.exceptions;

/**
 * An exception class representing errors that occur within the middleware.
 *
 * <p>This exception is a subclass of {@link RuntimeException} and is used to indicate
 * problems encountered during middleware processing. It provides a constructor to set
 * a descriptive error message.
 */
public class MiddlewareException extends RuntimeException {
        /**
     * Constructs a new MiddlewareException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     */
    public MiddlewareException(String message) {
        super(message);
    }
}
