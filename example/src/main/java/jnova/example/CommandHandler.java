package jnova.example;

import jnova.core.events.EventBus;
import jnova.tcp.TcpServer;
import jnova.tcp.handler.TcpRequestHandler;
import jnova.tcp.routing.commands.CommandHandlerFactory;


public class CommandHandler {
    public static void main(String[] args) throws Exception {
        TcpRequestHandler commandHandler = CommandHandlerFactory.createHandler("jnova.example.commands");
        TcpServer server = new TcpServer(commandHandler);
        server.start(7070);
        EventBus eventBus = server.getEventBus();

    }
}
