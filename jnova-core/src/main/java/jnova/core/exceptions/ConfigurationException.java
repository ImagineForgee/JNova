package jnova.core.exceptions;

/**
 * Exception indicating a configuration error.
 *
 * This exception is thrown when the application encounters a problem
 * related to its configuration, such as a missing or invalid setting.
 */
public class ConfigurationException extends RuntimeException {
        /**
     * Constructs a {@code ConfigurationException} with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     */
    public ConfigurationException(String message) {
        super(message);
    }
}
