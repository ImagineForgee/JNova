package jnova.tcp.routing.commands;

import jnova.tcp.TcpSession;

import java.util.Arrays;

public class TcpCommandDispatcher {
    private final TcpCommandRegistry registry;

    public TcpCommandDispatcher(TcpCommandRegistry registry) {
        this.registry = registry;
    }

    public void dispatch(String commandName, TcpSession session, String[] args) {
        registry.get(commandName).ifPresentOrElse(cmd -> {
            try {
                cmd.method().invoke(cmd.instance(), session, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, () -> {
            System.out.println("Unknown command: " + commandName);
        });
    }
}
