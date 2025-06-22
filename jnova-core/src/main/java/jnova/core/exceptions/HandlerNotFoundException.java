package jnova.core.exceptions;

/**
 * Exception thrown when a handler is not found.
 *
 * This exception is a subclass of {@link RuntimeException} and is used to indicate that a
 * handler could not be located for a given request or operation.
 */
public class HandlerNotFoundException extends RuntimeException {
        /**
     * Constructs a new HandlerNotFoundException with the specified message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public HandlerNotFoundException(String message) {
        super(message);
    }
}
