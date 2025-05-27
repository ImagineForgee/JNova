package jnova.tcp.framing;

import java.io.InputStream;
import java.util.function.Consumer;

public class DelimiterFraming implements FramingStrategy {
    private final byte delimiter;

    public DelimiterFraming(char delimiterChar) {
        this.delimiter = (byte) delimiterChar;
    }

    @Override
    public void readMessages(InputStream input, Consumer<byte[]> onMessage, Consumer<Throwable> onError) {
        try {
            StringBuilder buffer = new StringBuilder();
            int b;
            while ((b = input.read()) != -1) {
                if (b == delimiter) {
                    onMessage.accept(buffer.toString().getBytes());
                    buffer.setLength(0);
                } else {
                    buffer.append((char) b);
                }
            }
        } catch (Throwable e) {
            onError.accept(e);
        }
    }
}
