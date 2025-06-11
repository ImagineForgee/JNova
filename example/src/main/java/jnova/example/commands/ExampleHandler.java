package jnova.example.commands;

import jnova.annotations.tcp.TcpCommand;
import jnova.tcp.TcpSession;

public class ExampleHandler {
    @TcpCommand("PING")
    public void handlePing(TcpSession session, String[] args) {
        session.touch();
        session.send("YOUR_ALIVE".getBytes()).subscribe();
    }

    @TcpCommand("BROADCAST")
    public void handleBroadcast(TcpSession session, String[] args) {
        String msg = String.join(" ", args);
        session.broadcast("[Broadcast] " + msg + "\n");
    }
}
