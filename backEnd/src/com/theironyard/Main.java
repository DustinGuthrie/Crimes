package com.theironyard;
import jodd.json.JsonSerializer;
import spark.Session;
import spark.Spark;

import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by Agronis on 11/5/15.
 */
public class Main {

    // SQL Table Creation
    public static void createTables(Connection con) throws SQLException {
        Statement stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS crime (id IDENTITY, abbrev VARCHAR, name VARCHAR, year INT, population INT," +
                "total INT, murder INT, rape INT, robbery INT, assault INT)");
        stm.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, password VARCHAR, postCount INT, admin BOOLEAN, ip VARCHAR, access BOOLEAN)");
        stm.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, userId INT, crimeId INT, msgId INT, rating INT, text VARCHAR, time TIMESTAMP)");
    }

    // Inserting individual crime's into SQL Table "crime"
    public static void insertCrime(Connection con, Crime c) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO crime VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        stm.setString(1, c.abbrev);
        stm.setString(2, c.name);
        stm.setInt(3, c.year);
        stm.setInt(4, c.population);
        stm.setInt(5, c.total);
        stm.setInt(6, c.murder);
        stm.setInt(7, c.rape);
        stm.setInt(8, c.robbery);
        stm.setInt(9, c.assault);
        stm.execute();

    }

    // Method used to read CSV dump.
    static String readFile(String fileName) {
        File f = new File(fileName);
        try {
            FileReader fr = new FileReader(f);
            int fileSize = (int) f.length();
            char[] fileContent = new char[fileSize];
            fr.read(fileContent);
            return new String(fileContent);
        } catch (Exception e) {
            return null;
        }
    }

    // Method to parse CSV dump and populate database.
    public static void populateDatabase(Connection con) throws SQLException {
        String fileContent = readFile("backEnd/dump.csv");

        String[] lines = fileContent.split("\r");

        for (String line : lines) {
            String[] columns = line.split(",");
            Crime crime = new Crime(columns[0], columns[1], Integer.valueOf(columns[2]),
                    Integer.valueOf(columns[3]), Integer.valueOf(columns[4]), Integer.valueOf(columns[5]), Integer.valueOf(columns[6]),
                    Integer.valueOf(columns[7]), Integer.valueOf(columns[8]));
            insertCrime(con, crime);
        }
    }

    // Method to insert user into SQL Table "users"
    public static void insertUser(Connection con, User u) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?, ?, ?, ?, ?)");
        stm.setString(1, u.username);
        stm.setString(2, u.password);
        stm.setInt(3, u.postCount);
        stm.setBoolean(4, u.admin);
        stm.setString(5, u.ip);
        stm.setBoolean(6, u.access);
        stm.execute();
    }

    // Method to select a user from SQL.
    public static User selectUser(Connection con, String username) throws SQLException {
        User user = null;
        PreparedStatement stm = con.prepareStatement("SELECT * FROM users WHERE username = ?");
        stm.setString(1, username);
        ResultSet res = stm.executeQuery();
        if (res.next()) {
            user = new User();
            user.username = res.getString("username");
            user.password = res.getString("password");
            user.ip = res.getString("ip");
            user.admin = res.getBoolean("admin");
            user.access = res.getBoolean("access");
            user.postCount = res.getInt("postCount");
        }
        return user;
    }

    // Method to update user's post count.
    public static void editPostCount(Connection con, User u) throws SQLException {
        PreparedStatement stm = con.prepareStatement("UPDATE * FROM users SET postCount = ? WHERE id = ?");
        stm.setInt(1, u.postCount);
        stm.setInt(2, u.id);
        stm.executeUpdate();
    }

    // Method to ban user.
    public static void banUser(Connection con, String username) throws SQLException {
        PreparedStatement stm = con.prepareStatement("UPDATE * FROM users SET access = false WHERE username = ?");
        stm.setString(1, username);
        stm.executeUpdate();
    }


    // Method for inserting a new message to a crime object.
    public static void insertMsg(Connection con, Message m, User u, Crime c) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?, ?, ?, ?, ?)");
        stm.setInt(1, u.id);
        stm.setInt(2, c.id);
        stm.setInt(3, m.msgId);
        stm.setInt(4, m.rating);
        stm.setString(5, m.text);
        stm.setTimestamp(6, Timestamp.valueOf(m.timestamp.format(DateTimeFormatter.RFC_1123_DATE_TIME)));
        stm.execute();
    }

    //
    public static ArrayList<Message> selectMsgs(Connection con, int crimeId) throws SQLException {

        ArrayList<Message> selectMsgs = new ArrayList<>();
        PreparedStatement stm = con.prepareStatement(
                "SELECT * FROM messages WHERE messages.crimeId = ?");
        stm.setInt(1, crimeId);
        ResultSet results = stm.executeQuery();
        while (results.next()) {
            Message message = new Message();
            message.id = results.getInt("messages.id");
            message.userId = results.getInt("userId");
            message.crimeId = results.getInt("crimeId");
            message.msgId = results.getInt("msgId");
            message.text = results.getString("text");
            message.timestamp = results.getTimestamp("timestamp").toLocalDateTime();
            message.rating = results.getInt("rating");
            selectMsgs.add(message);
        }
        return selectMsgs;

    }

    public static Message selectMsg(Connection con, int id) throws SQLException {
        Message message = null;
        PreparedStatement stm = con.prepareStatement("SELECT * FROM messages INNER JOIN crime ON messages.crimeId = " +
                "crime.id WHERE messages.msgId = ?");
        stm.setInt(1, id);
        ResultSet results = stm.executeQuery();
        if (results.next()) {
            message = new Message();
            message.id = results.getInt("messages.id");
            message.userId = results.getInt("userId");
            message.crimeId = results.getInt("crimeId");
            message.msgId = results.getInt("msgId");
            message.text = results.getString("text");
            message.timestamp = results.getTimestamp("timestamp").toLocalDateTime();
            message.rating = results.getInt("rating");
        }
        return message;
    }

    // Method for deleting a message.
    public static void deleteMsg(Connection con, int msgId) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("DELETE FROM message WHERE msgId = ?");
        stmt.setInt(1, msgId);
        stmt.execute();
    }

    // Method for an Admin deleting a message.
    public static void adminDeleteMsg(Connection con, int msgId, Message m) throws SQLException {
        PreparedStatement stm = con.prepareStatement(
                "UPDATE message SET text = ?, rating = ?, userId = ?, timestamp = ? WHERE msgId = ?");
        stm.setString(1, m.text);
        stm.setInt(2, m.rating);
        stm.setInt(3, m.userId);
        stm.setTimestamp(4, Timestamp.valueOf(m.timestamp));
        stm.setInt(5, m.msgId);
        stm.executeUpdate();
    }

    // Method for editing a message & it's rating.  Keep's new time.
    public static void editMsg(Connection con, Message m) throws SQLException {
        PreparedStatement stm = con.prepareStatement(
                "UPDATE message SET text = ?, timestamp = ? WHERE msgId =" + m.msgId);
        stm.setString(1, m.text);
        stm.setTimestamp(2, Timestamp.valueOf(m.timestamp.format(DateTimeFormatter.RFC_1123_DATE_TIME)));

        stm.executeUpdate();
    }

    // Selecting an entire State's listing of crimes for all years.
    public static ArrayList<Crime> selectByName(Connection conn, String name) throws SQLException {
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM crime WHERE name = ?");
        stm.setString(1, name);
        ArrayList<Crime> crimes = new ArrayList();
        ResultSet results = stm.executeQuery();
        while(results.next()){
            Crime crime = new Crime();
            crime.name = results.getString("name");
            crime.abbrev = results.getString("abbrev");
            crime.year = results.getInt("year");
            crime.population = results.getInt("population");
            crime.total = results.getInt("total");
            crime.murder = results.getInt("murder");
            crime.rape = results.getInt("rape");
            crime.robbery = results.getInt("robbery");
            crime.assault = results.getInt("assault");
            crime.forum = results.getInt("forum");
            crimes.add(crime);
        }
        return crimes;
    }

    // Selecting ALL crimes from the SQL table.
    public static ArrayList<Crime> selectAll(Connection conn) throws SQLException {
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM crime");
        ArrayList<Crime> crimes = new ArrayList();
        ResultSet results = stm.executeQuery();
        while(results.next()){
            Crime crime = new Crime();
            crime.name = results.getString("name");
            crime.abbrev = results.getString("abbrev");
            crime.year = results.getInt("year");
            crime.population = results.getInt("population");
            crime.total = results.getInt("total");
            crime.murder = results.getInt("murder");
            crime.rape = results.getInt("rape");
            crime.robbery = results.getInt("robbery");
            crime.assault = results.getInt("assault");
            crime.forum = results.getInt("forum");
            crimes.add(crime);
        }
        return crimes;
    }

    // Selecting crimes strictly for one year from one state.
    public static Crime selectSingle(Connection conn, int year, String name) throws SQLException {
        Crime crime = null;
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM crime WHERE year = ?, name = ?");
        stm.setInt(1, year);
        stm.setString(2, name);
        ResultSet results = stm.executeQuery();
        if(results.next()){
            crime = new Crime();
            crime.name = results.getString("name");
            crime.abbrev = results.getString("abbrev");
            crime.year = results.getInt("year");
            crime.population = results.getInt("population");
            crime.total = results.getInt("total");
            crime.murder = results.getInt("murder");
            crime.rape = results.getInt("rape");
            crime.robbery = results.getInt("robbery");
            crime.assault = results.getInt("assault");
            crime.forum = results.getInt("forum");
        }
        return crime;
    }

    // Selecting all crime's for all state's for a particular year.
    public static ArrayList<Crime> selectYears(Connection conn, int year) throws SQLException {
        ArrayList<Crime> crimes = new ArrayList();
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM crime WHERE year = ?");
        stm.setInt(1, year);
        ResultSet results = stm.executeQuery();
        while (results.next()) {
            Crime crime = new Crime();
            crime.name = results.getString("name");
            crime.abbrev = results.getString("abbrev");
            crime.year = results.getInt("year");
            crime.population = results.getInt("population");
            crime.total = results.getInt("total");
            crime.murder = results.getInt("murder");
            crime.rape = results.getInt("rape");
            crime.robbery = results.getInt("robbery");
            crime.assault = results.getInt("assault");
            crime.forum = results.getInt("forum");
            crimes.add(crime);
        }
        return crimes;
    }

    // Main Argument
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:h2:./main");
        createTables(con);
        populateDatabase(con);
        Spark.externalStaticFileLocation("frontEnd");

        // Where the user initially lands when loading application.
        Spark.get(
                "/home",
                ((request, response) -> {
                    ArrayList<Crime> crime = selectAll(con);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(crime);
                })
        );

        // Login authentication.
        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                    String ip = request.ip();
                    User user = selectUser(con, username);

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
                        user.ip = ip;
                        if (ip.matches(user.ip)) {
                            Spark.halt(403);
                        }
                        insertUser(con, user);
                    } else if (!password.equals(user.password) || (!user.access)) {
                        Spark.halt(403);
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/home");
                    return "";
                })
        );


        // Method for banning a user.
        Spark.post(
                "/ban",
                ((request, response) -> {
                    String user = request.queryParams("banUser");
                    banUser(con, user);

                    response.redirect("/home");
                    return "";
                })

        );

//        // Method for loading forum entries.
//        Spark.get(
//                "/get-messages",
//                ((request, response) -> {
//                    String
//
//
//
//                  return "";
//                })
//        );

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

                    User u = selectUser(con, username);
                    Crime c = selectSingle(con, year, name);
                    Message m = new Message();

                    c.id = Integer.valueOf(request.queryParams("crimeId"));
                    m.text = request.queryParams("text");
                    m.crimeId = c.id;
                    m.msgId = 1;
                    m.userId = u.id;
                    m.rating = 1;
                    u.postCount = 1;
                    m.timestamp = LocalDateTime.now();
                    editPostCount(con, u);
                    insertMsg(con, m, u, c);

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

                    User u = selectUser(con, username);
                    Crime c = selectSingle(con, year, name);
                    Message m = new Message();

                    c.id = Integer.valueOf(request.queryParams("crimeId"));
                    m.text = request.queryParams("text");
                    m.crimeId = c.id;
                    m.msgId = m.msgId+ 1;
                    m.userId = u.id;
                    u.postCount = 1;
                    m.timestamp = LocalDateTime.now();
                    editPostCount(con, u);
                    insertMsg(con, m, u, c);

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


                    Message me = selectMsg(con, msgId);

                    if (username == null) {
                        Spark.halt(403);
                    }
                    me.text = request.queryParams("text");
                    String timestampStr = request.queryParams("timestamp");
                    try {
                        me.msgId = msgId;
                        me.timestamp = LocalDateTime.parse(timestampStr);
                        editMsg(con, me);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    response.redirect("/home");
                    return "";
                })
        );

        Spark.get(
                "/get-all",
                ((request, response) -> {
                    ArrayList<Crime> crime = selectAll(con);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(crime);
                })
        );

        Spark.get(
                "/get-years",
                ((request, response) -> {
                    String yearNum = request.queryParams("year");
                    int year = Integer.valueOf(yearNum);
                    ArrayList<Crime> crime = selectYears(con, year);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(crime);
                })
        );

        Spark.get(
                "/get-single",
                ((request, response) -> {
                    String yearNum = request.queryParams("year");
                    String name = request.queryParams("name");
                    try {
                        int year = Integer.valueOf(yearNum);
                        Crime crime = selectSingle(con, year, name);
                        JsonSerializer serializer = new JsonSerializer();
                        return serializer.serialize(crime);
                    } catch (Exception e) {

                    }
                    return "";
                })
        );

        Spark.get(
                "/get-graph",
                ((request, response) -> {
                    String name = request.queryParams("name");
                    ArrayList<Crime> crime = selectByName(con, name);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(crime);
                })
        );


    }

}
