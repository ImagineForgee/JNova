package jnova.tcp.framing;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A {@link FramingStrategy} that frames messages based on line boundaries.
 *
 * <p>This strategy reads messages from an input stream, treating each line as a separate message.
 * It also writes messages to an output stream, appending a newline character to each message.
 */
public class LineFraming implements FramingStrategy {

    /**
     * Reads messages from the given InputStream and emits them as a Flux of byte arrays.
     * <p>
     * Each line read from the input stream is converted to a byte array using UTF-8 encoding
     * and emitted as a separate element in the Flux. The Flux completes when the end of the
     * input stream is reached or an error occurs.
     *
     * @param input The InputStream to read messages from.
     * @return A Flux of byte arrays representing the messages read from the input stream.
     */
    @Override
    public Flux<byte[]> readMessages(InputStream input) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

        Schedulers.boundedElastic().schedule(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    queue.put(line);
                }
            } catch (IOException | InterruptedException e) {
                queue.offer("__EOF__");
            }
        });

        return Flux.generate(sink -> {
            try {
                String line = queue.take();
                if ("__EOF__".equals(line)) {
                    sink.complete();
                } else {
                    sink.next(line.getBytes(StandardCharsets.UTF_8));
                }
            } catch (InterruptedException e) {
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
