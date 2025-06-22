package jnova.example.commands;

import jakarta.validation.Valid;
import jnova.annotations.tcp.HandlerDeprecated;
import jnova.annotations.tcp.JsonArg;
import jnova.annotations.tcp.JsonProperty;
import jnova.annotations.tcp.TcpType;
import jnova.example.models.BroadcastMessage;
import jnova.tcp.TcpSession;
import jnova.tcp.protocol.TcpMessage;

import java.io.IOException;

@TcpType("command")
public class ExampleHandler {

    @JsonProperty(key = "command", value = "PING")
    public void handlePing(TcpSession session, @JsonArg("args") String[] args) {
        session.touch();
        TcpMessage json = new TcpMessage("keep-alive");
        session.send(json.toString().getBytes()).subscribe();
    }

    @HandlerDeprecated(since = "1988", message = "now")
    @JsonProperty(key = "command", value = "BROADCAST")
    public void handleBroadcast(@JsonArg("message") BroadcastMessage msg, @Valid TcpSession session) throws IOException {
        System.out.println("Received broadcast model: " + msg);
        session.broadcast((msg.toString() + "\n").getBytes());
    }
}
