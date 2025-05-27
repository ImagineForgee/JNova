package jnova.tcp.framing;

import java.io.InputStream;
import java.util.function.Consumer;

public interface FramingStrategy {
    void readMessages(InputStream input, Consumer<String> onMessage, Consumer<Throwable> onError);
}
