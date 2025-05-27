package jnova.example;

import jnova.tcp.TcpRequestHandler;
import jnova.tcp.TcpResponse;
import jnova.tcp.TcpServer;
import reactor.core.publisher.Mono;

public class Main {
    public static void main(String[] args) throws Exception {
        TcpRequestHandler handler = request -> {
            String input = request.getBody();
            String sessionId = request.getSession().getSessionId();

            String response = "[" + sessionId + "] You sent: " + input.toUpperCase();
            return Mono.just(new TcpResponse(response));
        };

        TcpServer server = new TcpServer(handler);
        server.start(7070);
    }
}
