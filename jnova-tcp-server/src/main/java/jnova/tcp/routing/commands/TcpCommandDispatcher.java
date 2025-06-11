package jnova.tcp.routing.commands;

import jnova.tcp.TcpSession;

import java.lang.reflect.Method;
import java.util.Arrays;

public class TcpCommandDispatcher {
    private final TcpCommandRegistry registry;

    public TcpCommandDispatcher(TcpCommandRegistry registry) {
        this.registry = registry;
    }

    public void dispatch(String commandName, TcpSession session, String[] args) {
        System.out.printf("Dispatching command: '%s' with args: %s%n", commandName, Arrays.toString(args));

        registry.get(commandName).ifPresentOrElse(cmd -> {
            Object instance = cmd.instance();
            Method method = cmd.method();
            System.out.printf("Found handler: %s.%s()%n", instance.getClass().getName(), method.getName());

            try {
                method.invoke(instance, session, args);
                System.out.printf("Successfully invoked command '%s'%n", commandName);
            } catch (Exception e) {
                System.err.printf("Error invoking command '%s': %s%n", commandName, e.getMessage());
                e.printStackTrace();
            }
        }, () -> {
            System.out.printf("Unknown command: '%s'%n", commandName);
        });
    }
}
