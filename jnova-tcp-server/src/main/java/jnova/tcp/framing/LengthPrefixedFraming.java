package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A {@link FramingStrategy} implementation that frames messages by prepending their length as an integer.
 *
 * <p>This strategy reads and writes messages to streams, prefixing each message with its length
 * encoded as an integer.  This allows for easy separation of messages within a stream.
 */
public class LengthPrefixedFraming implements FramingStrategy {
        /**
     * Reads messages from an input stream and emits them as a Flux of byte arrays.
     *
     * <p>This method reads the input stream, expecting each message to be prefixed with an integer
     * representing the length of the message. It then reads the specified number of bytes into a byte
     * array and emits it as a next signal in the Flux. The Flux completes when an EOFException is
     * encountered while reading the length, or if the sink is cancelled. Any other exceptions
     * encountered during the reading process are emitted as an error signal in the Flux.
     *
     * @param input The InputStream to read messages from.
     * @return A Flux of byte arrays representing the messages read from the input stream.
     */
    @Override
    public Flux<byte[]> readMessages(InputStream input) {
        return Flux.create(sink -> {
            DataInputStream dis = new DataInputStream(input);
            try {
                while (!sink.isCancelled()) {
                    int len;
                    try {
                        len = dis.readInt();
                    } catch (EOFException eof) {
                        sink.complete();
                        break;
                    }
                    byte[] data = new byte[len];
                    dis.readFully(data);
                    sink.next(data);
                }
            } catch (Throwable e) {
                sink.error(e);
            }
        });
    }

        /**
     * Writes a message to the output stream, prefixed with its length.
     *
     * @param output  The {@link OutputStream} to write the message to.
     * @param message The byte array containing the message to write.
     * @throws IOException If an I/O error occurs while writing to the stream.
     */
    @Override
    public void writeMessage(OutputStream output, byte[] message) throws IOException {
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(message.length);
        dos.write(message);
        dos.flush();
    }
}
