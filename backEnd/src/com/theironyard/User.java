package com.theironyard;

/**
 * Created by MattBrown on 11/6/15.
 */
public class User {
    int id;
    String username;
    String password;
    boolean admin;
    boolean access;
    String ip;
    int postCount;

    public User(){

    }

    public User(int id, String username, String password, boolean admin, boolean access, String ip, int postCount) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.admin = admin;
        this.access = access;
        this.ip = ip;
        this.postCount = postCount;
    }

    public int getId() {

        return id;
    }
    public String getUsername() {

        return username;
    }
    public String getPassword() {

        return password;
    }
    public boolean isAdmin() {

        return admin;
    }
    public boolean isAccess() {

        return access;
    }
    public String getIp() {

        return ip;
    }
    public int getPostCount() {

        return postCount;
    }
}
