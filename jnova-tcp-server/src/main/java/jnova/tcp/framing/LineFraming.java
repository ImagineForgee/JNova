package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class LineFraming implements FramingStrategy {

    @Override
    public Flux<byte[]> readMessages(InputStream input) {
        return Flux.create(sink -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null && !sink.isCancelled()) {
                    sink.next(line.getBytes(StandardCharsets.UTF_8));
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
        output.write('\n');  // newline delimiter
        output.flush();
    }
}
