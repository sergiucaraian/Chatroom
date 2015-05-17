package cc.chatclient;

public class Message {
    private String user, message;
    private boolean isSelf;

    public Message(){};

    public Message(String user, String message, boolean isSelf) {
        this.user = user;
        this.message = message;
        this.isSelf = isSelf;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSelf() {
        return isSelf;
    }
}
