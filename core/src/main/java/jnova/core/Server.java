package jnova.core;

public interface Server {
    void start(int port) throws Exception;
    void stop() throws Exception;
}