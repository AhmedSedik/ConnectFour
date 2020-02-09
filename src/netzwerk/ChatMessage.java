package netzwerk;

import java.io.Serializable;

/**
 * @author zozzy on 31.12.19
 */
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server.
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class ChatMessage implements Serializable {


    protected static final long serialVersionUID = 1112122200L;

    // The different types of message sent by the Client
    // MESSAGE an ordinary message
    // LOGOUT to disconnect from the Server
    static final int ONLINE_USERS = 0
            , MESSAGE = 1, LOGOUT = 2
            , PLAY_REQUEST = 3
            , RESPONSE_PLAY_REQUEST = 4
            , PLAY_CONNECT_FOUR = 5
            , REJECT_CONNECT_FOUR = 6
            , CLOSED = 10;


    private int type;

    private String message;

    String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private String sender;

    // constructor
    ChatMessage(int type, String message, String sender) {
        this.type = type;
        this.message = message;
        this.sender = sender;
    }


    int getType() {
        return type;
    }

    String getMessage() {
        return message;
    }

    void setMessage(String msg) {
        this.message = msg;
    }
}