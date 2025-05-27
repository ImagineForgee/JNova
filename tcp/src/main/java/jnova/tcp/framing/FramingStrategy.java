package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.function.Consumer;

public interface FramingStrategy {
    Flux<byte[]> readMessages(InputStream input);
}
