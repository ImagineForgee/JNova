package jnova.example;

import jnova.tcp.handler.TcpRequestHandler;
import jnova.tcp.TcpResponse;
import jnova.tcp.TcpServer;
import reactor.core.publisher.Mono;

public class NoCommandHandler {
    public static void main(String[] args) throws Exception {
        TcpRequestHandler handler = binaryRequest -> {
            byte[] input = binaryRequest.getData();
            String sessionId = binaryRequest.getSession().getId();
            String string = new String(input);

            System.out.println(string);

            byte[] uppercased = String.format("[%s] %s", sessionId, string)
                    .toUpperCase()
                    .getBytes();

            return Mono.just(new TcpResponse(uppercased));
        };

        TcpServer server = new TcpServer(handler);
        server.start(7070);
    }
}
