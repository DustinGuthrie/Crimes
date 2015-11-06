package com.theironyard;

/**
 * Created by MattBrown on 11/6/15.
 */
public class User {
    int id;
    String password;
    boolean admin;
    String ip;
    boolean access;
    String picture;

    public User(){

    }

    public User(int id, String password, boolean admin, String ip, boolean access, String picture) {
        this.id = id;
        this.password = password;
        this.admin = admin;
        this.ip = ip;
        this.access = access;
        this.picture = picture;
    }
}
