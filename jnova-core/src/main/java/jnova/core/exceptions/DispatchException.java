package jnova.core.exceptions;

/**
 * An exception that occurs during dispatching of events or commands.
 *
 * This exception is a subclass of {@link RuntimeException} and is used to
 * indicate errors that occur while routing or handling events or commands
 * within a system.
 */
public class DispatchException extends RuntimeException {
        /**
     * Constructs a new DispatchException with the specified message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public DispatchException(String message) {
        super(message);
    }
}
