package jnova.tcp.framing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class LineFraming implements FramingStrategy {

    @Override
    public void readMessages(InputStream input, Consumer<String> onMessage, Consumer<Throwable> onError) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                onMessage.accept(line);
            }
        } catch (Throwable e) {
            onError.accept(e);
        }
    }
}
