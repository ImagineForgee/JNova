package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A framing strategy that uses a delimiter character to separate messages.
 *
 * <p>This class implements the {@link FramingStrategy} interface and provides methods for
 * reading and writing messages using a specified delimiter. When reading, it accumulates bytes
 * until the delimiter is encountered, then emits the accumulated bytes as a single message.
 * When writing, it appends the delimiter to the end of each message.
 */
public class DelimiterFraming implements FramingStrategy {
    private final byte delimiter;

    /**
     * Constructs a DelimiterFraming instance with the specified delimiter character.
     *
     * @param delimiterChar The character used to delimit frames.  This will be cast to a byte.
     */
    public DelimiterFraming(char delimiterChar) {
        this.delimiter = (byte) delimiterChar;
    }

        /**
     * Reads messages from an input stream, splitting them based on a delimiter.
     *
     * @param input The input stream to read from.
     * @return A Flux of byte arrays, where each byte array represents a complete message.
     */
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

        /**
     * Writes a message to the output stream, followed by a delimiter, and flushes the stream.
     *
     * @param output  The {@link OutputStream} to write to.
     * @param message The byte array containing the message to write.
     * @throws IOException If an I/O error occurs during the write operation.
     */
    @Override
    public void writeMessage(OutputStream output, byte[] message) throws IOException {
        output.write(message);
        output.write(delimiter);
        output.flush();
    }
}
