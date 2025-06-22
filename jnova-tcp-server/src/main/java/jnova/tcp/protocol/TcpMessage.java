package jnova.tcp.protocol;

import com.google.gson.Gson;
import jnova.core.util.GsonFactory;

/**
 * Represents a TCP message with type, sender, and content.
 *
 * <p>This class is used for creating and serializing TCP messages for communication.
 * It includes fields for message type, sender identifier, and message content.
 * The class also provides methods for converting the message to a JSON string.
 */
public class TcpMessage {
    public String type;
    public String from;
    public String content;

    private static final Gson gson = GsonFactory.get();

        /**
     * Constructs a new TcpMessage object.
     */
    public TcpMessage() {}

        /**
     * Constructs a TcpMessage with the specified type.
     *
     * @param type The type of the TCP message.
     */
    public TcpMessage(String type) {
        this.type = type;
    }

        /**
     * Constructs a TcpMessage object.
     *
     * @param type    The type of the message.
     * @param content The content of the message.
     */
    public TcpMessage(String type, String content) {
        this.type = type;
        this.content = content;
    }

        /**
     * Constructs a TcpMessage object.
     *
     * @param type    The type of the message.
     * @param from    The sender of the message.
     * @param content The content of the message.
     */
    public TcpMessage(String type, String from, String content) {
        this.type = type;
        this.from = from;
        this.content = content;
    }

        /**
     * Returns a JSON string representation of this object.
     *
     * @return a JSON string representing this object, generated using Gson.
     */
    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
