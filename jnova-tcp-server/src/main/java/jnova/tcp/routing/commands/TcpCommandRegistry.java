package jnova.tcp.routing.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TcpCommandRegistry {
    private final Map<String, RegisteredCommand> commands = new HashMap<>();

    public void register(String name, Object instance, Method method) {
        commands.put(name.toUpperCase(), new RegisteredCommand(instance, method));
    }

    public Optional<RegisteredCommand> get(String name) {
        return Optional.ofNullable(commands.get(name.toUpperCase()));
    }

    public record RegisteredCommand(Object instance, Method method) {}
}
