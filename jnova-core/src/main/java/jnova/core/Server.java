package jnova.core;

import jnova.core.events.EventBus;

/**
 * An abstract base class for servers, providing basic lifecycle management
 * and an event bus for inter-component communication.
 *
 * <p>This class defines the core structure for server implementations,
 * including methods for starting and stopping the server, as well as
 * providing access to a shared event bus for publishing and subscribing
 * to events.
 */
public abstract class Server {
    private static final EventBus EVENT_BUS = new EventBus();

        /**
     * Starts the server on the specified port.
     *
     * @param port the port number to listen on
     * @throws Exception if an error occurs during server startup
     */
    public void start(int port) throws Exception {}
        /**
     * Stops the current process.
     *
     * @throws Exception if an error occurs during the stopping process
     */
    public void stop() throws Exception {}

        /**
     * Returns the singleton instance of the EventBus.
     *
     * @return The EventBus instance.
     */
    public static EventBus getEventBus() {
        return EVENT_BUS;
    }
}