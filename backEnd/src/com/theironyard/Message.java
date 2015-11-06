package com.theironyard;

/**
 * Created by MattBrown on 11/6/15.
 */
public class Message {
    int id;
    int replyId;
    String username;
        String text;

    public Message(){

    }

    public Message(int id, int replyId, String username, String text) {
            this.id = id;
            this.replyId = replyId;
            this.username = username;
            this.text = text;
    }
}
