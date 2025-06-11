package jnova.example;

import jnova.core.Server;
import jnova.core.events.EventBus;
import jnova.core.events.impl.ServerStartEvent;
import jnova.tcp.TcpServer;
import jnova.tcp.events.TcpMessageReceivedEvent;
import jnova.tcp.framing.DelimiterFraming;
import jnova.tcp.framing.LengthPrefixedFraming;
import jnova.tcp.handler.HandlerEnum;
import jnova.tcp.handler.TcpRequestHandler;
import jnova.tcp.routing.commands.CommandHandlerFactory;

import java.util.concurrent.Executors;


public class CommandHandler {
    public static void main(String[] args) throws Exception {
        TcpRequestHandler commandHandler = CommandHandlerFactory.createHandler("jnova.example.commands", HandlerEnum.JSON);
        TcpServer server = new TcpServer(commandHandler, Executors.newCachedThreadPool(), new DelimiterFraming('\n'));
        EventBus eventBus = Server.getEventBus();
        eventBus.onEvent(TcpMessageReceivedEvent.class).subscribe(e ->
                System.out.println(new String(e.getMessage())));
        server.start(7070);
    }
}
