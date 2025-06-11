package jnova.core;

import jnova.core.events.EventBus;

public abstract class Server {
    private static final EventBus EVENT_BUS = new EventBus();

    public void start(int port) throws Exception {}
    public void stop() throws Exception {}

    public static EventBus getEventBus() {
        return EVENT_BUS;
    }
}