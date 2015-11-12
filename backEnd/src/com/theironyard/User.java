package com.theironyard;

/**
 * Created by MattBrown on 11/6/15.
 */
public class User {
    int id;
    String username;
    String password;
    String ip;
    boolean admin = false;
    int postCount = 0;
    boolean access = true;

    public User(){

    }

    public User(String username){
        this.username = username;
    }

    public User(String username, String password, String ip) {

        this.username = username;
        this.password = password;
        this.ip = ip;
    }
    public User(int id, String username, String password, String ip, int postCount, boolean access, boolean admin) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.postCount = postCount;
        this.access = access;
        this.admin = admin;
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
