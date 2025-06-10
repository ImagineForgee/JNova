package jnova.example;

import jnova.core.Server;
import jnova.core.events.EventBus;
import jnova.core.events.impl.ServerStartEvent;
import jnova.tcp.TcpServer;
import jnova.tcp.handler.TcpRequestHandler;
import jnova.tcp.routing.commands.CommandHandlerFactory;


public class CommandHandler {
    public static void main(String[] args) throws Exception {
        TcpRequestHandler commandHandler = CommandHandlerFactory.createHandler("jnova.example.commands");
        TcpServer server = new TcpServer(commandHandler);
        EventBus eventBus = Server.getEventBus();
        eventBus.onEvent(ServerStartEvent.class)
                .subscribe(event -> System.out.printf("TCP server started on port %d at %s%n",
                        event.getPort(),
                        event.getTimestamp()));
        server.start(7070);
    }
}
