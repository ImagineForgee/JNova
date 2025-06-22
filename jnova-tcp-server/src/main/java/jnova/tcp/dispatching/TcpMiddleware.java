package jnova.tcp.dispatching;

import jnova.tcp.TcpSession;

import java.util.Map;

public interface TcpMiddleware {
    default void beforeDispatch(Map<String, Object> json, TcpSession session) {}
    default void afterDispatch(Map<String, Object> json, TcpSession session) {}
    default void onException(Throwable t, Map<String, Object> json, TcpSession session) {}
    default void onConnect(TcpSession session) {}
    default void onDisconnect(TcpSession session) {}
    default void onTimeout(TcpSession session) {}
    default void onProtocolError(Throwable t, TcpSession session) {}
}
