package jnova.core.exceptions;

/**
 * Exception thrown when a required field is missing.
 *
 * <p>This exception is a subclass of {@link RuntimeException}, indicating that it is an
 * unchecked exception. It is typically used to signal that a necessary field or
 * attribute is absent during object creation or processing.
 */
public class MissingFieldException extends RuntimeException {
        /**
     * Constructs a new MissingFieldException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     */
    public MissingFieldException(String message) {
        super(message);
    }
}
