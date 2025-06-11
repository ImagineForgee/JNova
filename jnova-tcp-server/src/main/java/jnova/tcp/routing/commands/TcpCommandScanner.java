package jnova.tcp.routing.commands;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import jnova.annotations.tcp.TcpCommand;

import java.lang.reflect.Method;

public class TcpCommandScanner {

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
