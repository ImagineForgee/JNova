package jnova.example.models;

public class BroadcastMessage {
    private String type;
    private String sender;
    private String content;

    public BroadcastMessage() {}

    public String getType()       { return type; }
    public void setType(String t) { this.type = t; }

    public String getSender()         { return sender; }
    public void setSender(String s)   { this.sender = s; }

    public String getContent()           { return content; }
    public void setContent(String c)     { this.content = c; }

    @Override
    public String toString() {
        return "BroadcastMessage{" +
                "type='" + type + '\'' +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
