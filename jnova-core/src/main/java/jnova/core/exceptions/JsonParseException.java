package jnova.core.exceptions;

/**
 * Exception thrown when there is an error parsing JSON data.
 *
 * <p>This exception is typically thrown when the JSON data is malformed or
 * does not conform to the expected structure.</p>
 */
public class JsonParseException extends RuntimeException {
        /**
     * Constructs a new JsonParseException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     */
    public JsonParseException(String message) {
        super(message);
    }
}
