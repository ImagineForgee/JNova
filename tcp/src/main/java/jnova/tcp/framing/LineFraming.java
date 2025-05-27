package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
}
