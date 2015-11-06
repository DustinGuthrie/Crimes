package com.theironyard;

/**
 * Created by MattBrown on 11/6/15.
 */
public class User {
    int id;
    String username;
    String password;
    boolean admin;

    public User(){

    }

    public User(int id, String username, String password, boolean admin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.admin = admin;
    }
}
