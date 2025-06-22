package jnova.core.exceptions;

/**
 * Exception thrown when a validation error occurs.
 *
 * This exception is typically used to indicate that data or input failed to meet
 * specified validation criteria.
 */
public class ValidationException extends RuntimeException {
        /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *     by the {@link #getMessage()} method).
     */
    public ValidationException(String message) {
        super(message);
    }
}
