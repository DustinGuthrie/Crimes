package com.theironyard;

import java.time.LocalDateTime;

/**
 * Created by MattBrown on 11/6/15.
 */
public class Message {
    int id;
    int userId;
    int crimeId;
    int msgId;
    String text;
    int rating;
    LocalDateTime timestamp;

    String username;

    public Message(){

    }
    public Message(int userId, int crimeId, int msgId, String text, int rating, LocalDateTime timestamp) {

        this.userId = userId;
        this.crimeId = crimeId;
        this.msgId = msgId;
        this.text = text;
        this.rating = rating;
        this.timestamp = timestamp;
    }
    public int getId() {

        return id;
    }

    public String getText() {

        return text;
    }
    public LocalDateTime getTimestamp() {

        return timestamp;
    }

    public int getRating() {

        return rating;
    }

    public Message(int id, int userId, int crimeId, int msgId, String text, int rating, LocalDateTime timestamp) {

        this.id = id;
        this.userId = userId;
        this.crimeId = crimeId;
        this.msgId = msgId;
        this.text = text;
        this.rating = rating;
        this.timestamp = timestamp;
    }
}
