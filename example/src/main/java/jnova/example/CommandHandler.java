package jnova.example;

import jnova.core.Server;
import jnova.core.events.EventBus;
import jnova.core.events.impl.ServerStartEvent;
import jnova.tcp.TcpServer;
import jnova.tcp.dispatching.resolvers.FromSessionResolver;
import jnova.tcp.dispatching.resolvers.JsonArgResolver;
import jnova.tcp.dispatching.resolvers.JsonBodyResolver;
import jnova.tcp.events.TcpMessageReceivedEvent;
import jnova.tcp.framing.DelimiterFraming;
import jnova.tcp.framing.LineFraming;
import jnova.tcp.handler.HandlerEnum;
import jnova.tcp.handler.TcpRequestHandler;
import jnova.tcp.routing.commands.DispatcherFactory;
import jnova.tcp.service.DispatcherReportService;

import java.util.List;
import java.util.concurrent.Executors;


public class CommandHandler {
    public static void main(String[] args) throws Exception {
        TcpRequestHandler handler = DispatcherFactory
                .builder()
                .basePackage("jnova.example.commands")
                .resolvers(List.of(new JsonArgResolver(), new FromSessionResolver(), new JsonBodyResolver()))
                .handlerType(HandlerEnum.JSON)
                .build();
        TcpServer server = new TcpServer(handler);
        EventBus eventBus = Server.getEventBus();
        eventBus.onEvent(ServerStartEvent.class).subscribe(e ->
                System.out.println(DispatcherReportService.toFancyReport()));
        eventBus.onEvent(TcpMessageReceivedEvent.class).subscribe(e ->
                System.out.println(new String(e.getMessage())));
        server.start(7070);
    }
}
