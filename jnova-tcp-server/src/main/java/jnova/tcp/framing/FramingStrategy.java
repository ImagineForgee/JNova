package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FramingStrategy {
    Flux<byte[]> readMessages(InputStream input);
    void writeMessage(OutputStream output, byte[] message) throws IOException;
}
