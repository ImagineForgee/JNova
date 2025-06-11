package jnova.example.commands;

import jnova.annotations.tcp.TcpCommand;
import jnova.tcp.TcpSession;
import jnova.tcp.protocol.TcpMessage;

public class ExampleHandler {
    @TcpCommand("PING")
    public void handlePing(TcpSession session, String[] args) {
        session.touch();
        TcpMessage json = new TcpMessage("keep-alive");
        session.send(json.toString().getBytes()).subscribe();
    }

    @TcpCommand("BROADCAST")
    public void handleBroadcast(TcpSession session, String[] args) {
        String msg = String.join(" ", args);
        TcpMessage json = new TcpMessage("message", "Carson", msg);

        session.broadcast((json + "\n").getBytes());
    }
}
