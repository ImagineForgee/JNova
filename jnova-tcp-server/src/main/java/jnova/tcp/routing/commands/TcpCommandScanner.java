package jnova.tcp.routing.commands;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import jnova.annotations.tcp.TcpCommand;

import java.lang.reflect.Method;

/**
 * Scans a package for classes and methods annotated with {@link TcpCommand} and registers them with a {@link TcpCommandRegistry}.
 *
 * <p>This class uses ClassGraph to scan the specified package and its subpackages for classes.
 * For each class found, it instantiates the class and then iterates through its methods.
 * If a method is annotated with {@code @TcpCommand}, the command name is extracted from the annotation
 * and the method is registered with the provided {@link TcpCommandRegistry}.
 */
public class TcpCommandScanner {

        /**
     * Scans a given base package for classes annotated with {@link TcpCommand} and registers
     * the corresponding methods with the provided {@link TcpCommandRegistry}.
     *
     * <p>This method uses ClassGraph to scan the specified package for classes. For each class,
     * it instantiates the class, and then iterates through its methods, registering any method
     * annotated with {@link TcpCommand} with the provided {@link TcpCommandRegistry}. The name
     * of the command is obtained from the {@link TcpCommand} annotation's value.
     *
     * @param basePackage The base package to scan for classes.
     * @param registry The {@link TcpCommandRegistry} to register the commands with.
     * @throws RuntimeException if the scanning or registration process fails.
     */
    public static void scanAndRegister(String basePackage, TcpCommandRegistry registry) {
        try (ScanResult scan = new ClassGraph().enableAllInfo().acceptPackages(basePackage).scan()) {
            for (ClassInfo classInfo : scan.getAllClasses()) {
                Class<?> clazz = classInfo.loadClass();
                Object instance = clazz.getDeclaredConstructor().newInstance();

                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(TcpCommand.class)) {
                        String commandName = method.getAnnotation(TcpCommand.class).value();
                        registry.register(commandName, instance, method);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan TCP commands", e);
        }
    }
}
