package com.theironyard;

import java.time.LocalDateTime;

/**
 * Created by MattBrown on 11/6/15.
 */
public class Message {
    int id;
    int replyId;
    String username;
    String text;
    LocalDateTime time;
    int rating;

    public Message(){

    }

    public int getId() {

        return id;
    }
    public int getReplyId() {

        return replyId;
    }
    public String getUsername() {

        return username;
    }
    public String getText() {

        return text;
    }
    public LocalDateTime getTime() {

        return time;
    }

    public int getRating() {

        return rating;
    }
    public Message(int id, int replyId, String username, String text, LocalDateTime time, int rating) {


        this.id = id;
        this.replyId = replyId;
        this.username = username;
        this.text = text;
        this.time = time;
        this.rating = rating;
    }
}
