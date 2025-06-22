package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Strategy interface for framing messages read from and written to streams.
 *
 * Defines methods for reading a stream of byte arrays (messages) from an input stream,
 * and writing a single byte array message to an output stream.  Implementations handle
 * the specific details of how messages are delimited or framed within the stream.
 */
public interface FramingStrategy {
        /**
     * Reads messages from an input stream as a reactive stream of byte arrays.
     *
     * @param input The input stream to read messages from.
     * @return A {@link Flux} emitting byte arrays representing the messages read from the input stream.
     */
    Flux<byte[]> readMessages(InputStream input);
        /**
     * Writes a byte array message to the given output stream.
     *
     * @param output The output stream to write to.
     * @param message The byte array containing the message to write.
     * @throws IOException If an I/O error occurs during the write operation.
     */
    void writeMessage(OutputStream output, byte[] message) throws IOException;
}
