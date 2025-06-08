package jnova.tcp.routing.commands;

import jnova.tcp.handler.CommandRequestHandler;
import jnova.tcp.handler.TcpRequestHandler;

public class CommandHandlerFactory {
    public static TcpRequestHandler createHandler(String basePackage) {
        TcpCommandRegistry registry = new TcpCommandRegistry();
        TcpCommandScanner.scanAndRegister(basePackage, registry);
        TcpCommandDispatcher dispatcher = new TcpCommandDispatcher(registry);
        return new CommandRequestHandler(dispatcher);
    }
}
