package jnova.tcp.routing.commands;

import jnova.tcp.TcpSession;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Dispatches TCP commands to their corresponding handlers.
 *
 * <p>This class receives a command name, a TCP session, and arguments, then uses a
 * {@link TcpCommandRegistry} to find the appropriate command handler and execute it.
 */
public class TcpCommandDispatcher {
    private final TcpCommandRegistry registry;

        /**
     * Constructs a new TcpCommandDispatcher with the given registry.
     *
     * @param registry The TcpCommandRegistry to use for command lookup.
     */
    public TcpCommandDispatcher(TcpCommandRegistry registry) {
        this.registry = registry;
    }

        /**
     * Dispatches a command to its corresponding handler.
     *
     * <p>This method retrieves the command handler from the registry based on the provided
     * command name. If a handler is found, it invokes the corresponding method with the
     * given TCP session and arguments. If no handler is found, it prints an "unknown command"
     * message. Any exceptions during invocation are caught and logged.
     *
     * @param commandName The name of the command to dispatch.
     * @param session     The TCP session associated with the command.
     * @param args        The arguments for the command.
     */
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
