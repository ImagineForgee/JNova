package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LengthPrefixedFraming implements FramingStrategy {
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

    @Override
    public void writeMessage(OutputStream output, byte[] message) throws IOException {
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(message.length);
        dos.write(message);
        dos.flush();
    }
}
