package jnova.core.events;

/**
 * Represents the different types of events that can occur in the system.
 */
public enum EventType {
    /**
     * The event triggered when the server starts.
     */
    SERVER_START,
    /**
     * The event triggered when the server stops.
     */
    SERVER_STOP,
    /**
     * The event triggered when the server encounters an error.
     */
    SERVER_ERROR,
    /**
     * The event triggered when a TCP session is opened.
     */
    TCP_SESSION_OPEN,
    /**
     * The event triggered when a TCP session is closed.
     */
    TCP_SESSION_CLOSE,
    /**
     * The event triggered when a TCP session encounters an error.
     */
    TCP_SESSION_ERROR,
    /**
     * The event triggered when a TCP message is received.
     */
    TCP_MESSAGE_RECEIVED,
    /**
     * A custom event type for user-defined events.
     */
    CUSTOM
}

