package jnova.example.commands;

import jnova.annotations.tcp.TcpCommand;
import jnova.tcp.TcpSession;

public class ExampleHandler {
    @TcpCommand("PING")
    public void handlePing(TcpSession session, String[] args) {
        session.keepAlive();
        session.send("PONG".getBytes()).subscribe();
    }
}
