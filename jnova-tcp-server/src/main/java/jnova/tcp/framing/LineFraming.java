package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * A {@link FramingStrategy} that frames messages based on line boundaries.
 *
 * <p>This strategy reads messages from an input stream, treating each line as a separate message.
 * It also writes messages to an output stream, appending a newline character to each message.
 */
public class LineFraming implements FramingStrategy {

        /**
     * Reads messages from the given InputStream and emits them as a Flux of byte arrays.
     *
     * Each line read from the input stream is converted to a byte array using UTF-8 encoding
     * and emitted as a separate element in the Flux. The Flux completes when the end of the
     * input stream is reached or an error occurs.
     *
     * @param input The InputStream to read messages from.
     * @return A Flux of byte arrays representing the messages read from the input stream.
     */
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

        /**
     * Writes a message to the given output stream, followed by a newline character.
     *
     * @param output  The output stream to write to.
     * @param message The message to write as a byte array.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void writeMessage(OutputStream output, byte[] message) throws IOException {
        output.write(message);
        output.write('\n');
        output.flush();
    }
}
