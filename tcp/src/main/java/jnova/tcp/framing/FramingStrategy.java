package jnova.tcp.framing;

import java.io.InputStream;
import java.util.function.Consumer;

public interface FramingStrategy {
    void readMessages(InputStream input, Consumer<byte[]> onMessage, Consumer<Throwable> onError);
}
