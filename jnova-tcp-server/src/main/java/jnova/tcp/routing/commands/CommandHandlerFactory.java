package jnova.tcp.routing.commands;

import jnova.tcp.handler.CommandRequestHandler;
import jnova.tcp.handler.HandlerEnum;
import jnova.tcp.handler.JsonTcpRequestHandler;
import jnova.tcp.handler.TcpRequestHandler;

import java.util.function.Consumer;

public class CommandHandlerFactory {
    public static TcpRequestHandler createHandler(String basePackage, HandlerEnum handlerEnum) {
        TcpCommandRegistry registry = new TcpCommandRegistry();
        TcpCommandScanner.scanAndRegister(basePackage, registry);
        TcpCommandDispatcher dispatcher = new TcpCommandDispatcher(registry);
        return switch (handlerEnum) {
            case JSON -> new JsonTcpRequestHandler(dispatcher);
            case COMMAND -> new CommandRequestHandler(dispatcher);
        };
    }
}
