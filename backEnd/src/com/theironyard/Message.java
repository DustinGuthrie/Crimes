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
    String username;
    String text;
    int rating;
    LocalDateTime timestamp;

    public Message(){

    }

    public int getId() {

        return id;
    }
    public String getUsername() {

        return username;
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

    public Message(int id, int userId, int crimeId, int msgId, String text, LocalDateTime timestamp, int rating) {
        this.id = id;
        this.userId = userId;
        this.crimeId = crimeId;
        this.msgId = msgId;
        this.text = text;
        this.timestamp = timestamp;
        this.rating = rating;
    }

    public Message(int id, int userId, int crimeId, String username, String text, LocalDateTime timestamp, int rating) {

        this.id = id;
        this.userId = userId;
        this.crimeId = crimeId;
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
        this.rating = rating;
    }
}
