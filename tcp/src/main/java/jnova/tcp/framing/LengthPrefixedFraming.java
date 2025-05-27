package jnova.tcp.framing;

import reactor.core.publisher.Flux;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.function.Consumer;

public class LengthPrefixedFraming implements FramingStrategy {
    @Override
    public Flux<byte[]> readMessages(InputStream input) {
        return Flux.create(sink -> {
            try (DataInputStream dis = new DataInputStream(input)) {
                while (!sink.isCancelled()) {
                    int len = dis.readInt();
                    byte[] data = new byte[len];
                    dis.readFully(data);
                    sink.next(data);
                }
                sink.complete();
            } catch (Throwable e) {
                sink.error(e);
            }
        });
    }
}
