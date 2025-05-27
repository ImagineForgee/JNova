package jnova.tcp.framing;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.function.Consumer;

public class LengthPrefixedFraming implements FramingStrategy {
    @Override
    public void readMessages(InputStream input, Consumer<byte[]> onMessage, Consumer<Throwable> onError) {
        try (DataInputStream dis = new DataInputStream(input)) {
            while (true) {
                int len = dis.readInt();
                byte[] data = new byte[len];
                dis.readFully(data);
                onMessage.accept(data);
            }
        } catch (Throwable e) {
            onError.accept(e);
        }
    }
}
