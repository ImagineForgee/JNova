package jnova.tcp.protocol;

import com.google.gson.Gson;
import jnova.core.util.GsonFactory;

public class TcpMessage {
    public String type;
    public String from;
    public String content;

    private static final Gson gson = GsonFactory.get();

    public TcpMessage() {}

    public TcpMessage(String type) {
        this.type = type;
    }

    public TcpMessage(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public TcpMessage(String type, String from, String content) {
        this.type = type;
        this.from = from;
        this.content = content;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
