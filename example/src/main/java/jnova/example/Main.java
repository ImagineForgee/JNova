package jnova.example;

import jnova.tcp.request.TcpRequestHandler;
import jnova.tcp.TcpResponse;
import jnova.tcp.TcpServer;
import reactor.core.publisher.Mono;

public class Main {
    public static void main(String[] args) throws Exception {
        TcpRequestHandler handler = binaryRequest -> {
            byte[] input = binaryRequest.getData();
            String sessionId = binaryRequest.getSession().getSessionId();
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
