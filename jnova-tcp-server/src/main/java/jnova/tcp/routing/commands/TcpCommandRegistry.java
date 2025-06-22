package jnova.tcp.routing.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *  Registry for TCP commands, mapping command names to their corresponding methods.
 *
 *  This class allows registering and retrieving command handlers for processing TCP requests.
 */
public class TcpCommandRegistry {
    private final Map<String, RegisteredCommand> commands = new HashMap<>();

        /**
     * Registers a command with a name, instance, and method.
     *
     * @param name The name of the command (case-insensitive).
     * @param instance The object instance on which the method is invoked.
     * @param method The method to be executed when the command is called.
     */
    public void register(String name, Object instance, Method method) {
        commands.put(name.toUpperCase(), new RegisteredCommand(instance, method));
    }

        /**
     * Retrieves a registered command by its name.
     *
     * @param name The name of the command to retrieve (case-insensitive).
     * @return An Optional containing the RegisteredCommand if found, or an empty Optional if not found.
     */
    public Optional<RegisteredCommand> get(String name) {
        return Optional.ofNullable(commands.get(name.toUpperCase()));
    }

    public record RegisteredCommand(Object instance, Method method) {}
}
