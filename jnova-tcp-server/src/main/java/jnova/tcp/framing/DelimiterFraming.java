package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class DelimiterFraming implements FramingStrategy {
    private final byte delimiter;

    public DelimiterFraming(char delimiterChar) {
        this.delimiter = (byte) delimiterChar;
    }

    @Override
    public Flux<byte[]> readMessages(InputStream input) {
        return Flux.create(sink -> {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int b;
                while ((b = input.read()) != -1 && !sink.isCancelled()) {
                    if (b == delimiter) {
                        sink.next(buffer.toByteArray());
                        buffer.reset();
                    } else {
                        buffer.write(b);
                    }
                }
                sink.complete();
            } catch (Throwable e) {
                sink.error(e);
            }
        });
    }

    @Override
    public void writeMessage(OutputStream output, byte[] message) throws IOException {
        output.write(message);
        output.write(delimiter);
        output.flush();
    }
}
