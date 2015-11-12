package com.theironyard;
import jodd.json.JsonSerializer;
import spark.Session;
import spark.Spark;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Agronis on 11/5/15.
 */
public class Main {

    // Main Argument
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:h2:./main");
        Methods.createTables(con);
        Methods.populateDatabase(con);
        Spark.externalStaticFileLocation("frontEnd");

        // Where the user initially lands when loading application.
        Spark.get(
                "/home",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    User u = new User();
                    u.username = username;
                    HashMap <User, ArrayList<Crime>> map = new HashMap();
                    ArrayList<Crime> crime = Methods.selectAll(con);
                    map.put(u, crime);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(crime);
                })
        );

        Spark.get(
                "/test",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    User u = new User();
                    u.username = username;
                    HashMap <User, ArrayList<Crime>> map = new HashMap();
                    ArrayList<Crime> crime = Methods.selectAll(con);
                    map.put(u, crime);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(map);
                })
        );

        // Login authentication.
        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                    User user = Methods.selectUser(con, username);

                    if (username.isEmpty() || password.isEmpty()) {
                        Spark.halt(403);
                    }

                    if (user == null) {
                        user = new User();
                        user.username = username;
                        user.password = password;
                        if (user.password.equals("admin")) {
                            user.admin = true;
                        }
                        Methods.insertUser(con, user);
                    } else if (!password.equals(user.password) || (!user.access)) {
                        Spark.halt(403);
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";
                })
        );


        // Method for banning a user.
        Spark.post(
                "/ban",
                ((request, response) -> {
                    String user = request.queryParams("banUser");
                    Methods.banUser(con, user);

                    response.redirect("/home");
                    return "";
                })

        );

        // Method for loading forum entries.
        Spark.get(
                "/get-messages",
                ((request, response) -> {
                    String id = request.queryParams("crimeId");
                    try {
                        int idNum = Integer.valueOf(id);
                        ArrayList<Message> msgs = Methods.selectMsgs(con, idNum);
                        JsonSerializer serializer = new JsonSerializer();
                        return serializer.serialize(msgs);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                  return "";
                })
        );

        // Method for posting forum entries.
        Spark.post(
                "/create-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null){
                        Spark.halt(403);
                    }

                    String name = request.queryParams("name");
                    int year = Integer.valueOf(request.queryParams("year"));

                    User u = Methods.selectUser(con, username);
                    Crime c = Methods.selectSingle(con, year, name);
                    Message m = new Message();

                    m.text = request.queryParams("text");
                    m.crimeId = c.id;
                    m.userId = u.id;
                    m.msgId = 1;
                    m.text = request.queryParams("text");
                    m.rating = 1;
                    u.postCount = u.postCount + 1;
                    m.timestamp = LocalDateTime.now();
                    Methods.insertMsg(con, u.id, c.id, m.msgId, m.text, m.rating, m.timestamp);
                    Methods.editPostCount(con, u);

                    session.attribute("username", username);
                    session.attribute("crimeId", c.id);

                    response.redirect("/home");
                    return "";

                }
        );

        // Method for replying to forum entries.
        Spark.post(
                "/create-reply",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null){
                        Spark.halt(403);
                    }

                    String name = request.queryParams("name");
                    int year = Integer.valueOf(request.queryParams("year"));

                    User u = Methods.selectUser(con, username);
                    Crime c = Methods.selectSingle(con, year, name);
                    Message m = new Message();

                    c.id = Integer.valueOf(request.queryParams("crimeId"));
                    m.text = request.queryParams("text");
                    m.crimeId = c.id;
                    m.msgId = m.msgId + 1;
                    m.userId = u.id;
                    u.postCount = u.postCount + 1;
                    m.timestamp = LocalDateTime.now();
                    Methods.editPostCount(con, u);
                    Methods.insertMsg(con, u.id, c.id, m.msgId, m.text, m.rating, m.timestamp);

                    session.attribute("username", username);
                    session.attribute("crimeId", c.id);

                    response.redirect("/home");
                    return "";
                })

        );

        // Method for updating forum rating.
        Spark.post(
                "/edit-message",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    int msgId = Integer.valueOf(request.queryParams("msgId"));


                    Message me = Methods.selectMsg(con, msgId);

                    if (username == null) {
                        Spark.halt(403);
                    }
                    me.text = request.queryParams("text");
                    String timestampStr = request.queryParams("timestamp");
                    try {
                        me.msgId = msgId;
                        me.timestamp = LocalDateTime.parse(timestampStr);
                        Methods.editMsg(con, me);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    session.attribute("crimeId", me.crimeId);

                    response.redirect("/home");
                    return "";
                })
        );

        Spark.get(
                "/get-all",
                ((request, response) -> {
                    ArrayList<Crime> crime = Methods.selectAll(con);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(crime);
                })
        );

        Spark.get(
                "/get-years",
                ((request, response) -> {
                    String yearNum = request.queryParams("year");
                    try {
                        int year = Integer.valueOf(yearNum);
                        ArrayList<Crime> crime = Methods.selectYears(con, year);
                        JsonSerializer serializer = new JsonSerializer();
                        return serializer.serialize(crime);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    return "";
                })
        );

        Spark.get(
                "/get-single",
                ((request, response) -> {
                    String yearNum = request.queryParams("year");
                    String name = request.queryParams("name");
                    try {
                        int year = Integer.valueOf(yearNum);
                        Crime crime = Methods.selectSingle(con, year, name);
                        JsonSerializer serializer = new JsonSerializer();
                        return serializer.serialize(crime);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    return "";
                })
        );

        Spark.get(
                "/get-graph",
                ((request, response) -> {
                    String name = request.queryParams("name");
                    ArrayList<Crime> crime = Methods.selectByName(con, name);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(crime);
                })
        );

        Spark.post(
                "/rating",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    String rating = request.queryParams("rating");
                    String message = request.queryParams("msgId");
                    try {
                        int ratingNum = Integer.valueOf(rating);
                        int msgNum = Integer.valueOf(message);
                        Message m = Methods.selectMsg(con, msgNum);
                        m.rating = m.rating + ratingNum;
                        Methods.editMsg(con, m);
                        session.attribute("crimeId", m.crimeId);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    response.redirect("/home");
                    return "";
                })

        );

        Spark.post(
                "/post-msg",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    String text = request.queryParams("text");
                    String time = request.queryParams("timestamp");
                    Message m = new Message();
                    User u = Methods.selectUser(con, username);
                    m.userId = u.id;
                    m.text = text;
                    m.timestamp = LocalDateTime.parse(time);
                    Methods.insMsg(con, m);

                    session.attribute("username", username);
                    response.redirect("/home");
                    return "";
                })

        );

        Spark.get(
                "/get-msg",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    ArrayList<Message> msgs = Methods.getMsg(con);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(msgs);
                })
        );


    }

}
