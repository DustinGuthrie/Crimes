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
                "total INT, murder INT, rape INT, robbery INT, assault INT, forum INT)");
        stm.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, password VARCHAR, postCount INT, admin BOOLEAN, ip VARCHAR, access BOOLEAN)");
        stm.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, replyID INT, username VARCHAR, rating INT, text VARCHAR, time TIMESTAMP)");
    }

    // Inserting individual crime's into SQL Table "crime"
    public static void insertCrime(Connection con, Crime c) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO crime VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        stm.setString(1, c.name);
        stm.setString(2, c.abbrev);
        stm.setInt(3, c.year);
        stm.setInt(4, c.population);
        stm.setInt(5, c.total);
        stm.setInt(6, c.murder);
        stm.setInt(7, c.rape);
        stm.setInt(8, c.robbery);
        stm.setInt(9, c.assault);
        stm.setInt(10, c.forum);
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

    public static User selectIP(Connection con, String ip) throws SQLException {
        User user = null;
        PreparedStatement stm = con.prepareStatement("SELECT * FROM users WHERE ip = ?");
        stm.setString(1, ip);
        ResultSet res = stm.executeQuery();
        if (res.next()) {
            user = new User();
            user.ip = res.getString("ip");
        }
        return user;
    }

    public static void insertMsg(Connection con, int user_id, int reply_id, String text, int rating, LocalDateTime timestamp) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?, ?, ?, ?)");
        stm.setInt(1, user_id);
        stm.setInt(2, reply_id);
        stm.setString(3, text);
        stm.setInt(4, rating);
        stm.setTimestamp(5, Timestamp.valueOf(timestamp.format(DateTimeFormatter.RFC_1123_DATE_TIME)));
        stm.execute();
    }

    public static ArrayList<Message> selectReplies(Connection con, int replyId) throws SQLException {

        ArrayList<Message> selectReplies = new ArrayList<>();
        PreparedStatement stm = con.prepareStatement(
                "SELECT * FROM messages INNER JOIN users ON messages.user_id = users.id WHERE messages.reply_id = ?");
        stm.setInt(1, replyId);
        ResultSet results = stm.executeQuery();
        while (results.next()) {
            Message message = new Message();
            message.id = results.getInt("messages.id");
            message.replyId = results.getInt("messages.reply_id");
            message.username = results.getString("users.name");
            message.text = results.getString("text");
            message.time = results.getTimestamp("timestamp").toLocalDateTime();
            message.rating = results.getInt("rating");
            selectReplies.add(message);
        }
        return selectReplies;

    }

    public static Message selectMsg(Connection con, int id) throws SQLException {
        Message message = null;
        PreparedStatement stm = con.prepareStatement("SELECT * FROM messages INNER JOIN users ON messages.user_id = " +
                "users.id WHERE messages.id = ?");
        stm.setInt(1, id);
        ResultSet results = stm.executeQuery();
        if (results.next()) {
            message = new Message();
            message.id = results.getInt("messages.id");
            message.replyId = results.getInt("messages.reply_id");
            message.username = results.getString("users.name");
            message.text = results.getString("text");
            message.time = results.getTimestamp("timestamp").toLocalDateTime();
            message.rating = results.getInt("rating");
        }
        return message;
    }

    // Selecting an entire State's listing of crimes for all years.
    public static ArrayList<Crime> selectStateCrimes(Connection conn, String name) throws SQLException {
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
    public static Crime selectYear(Connection conn, int year, String name) throws SQLException {
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
        Crime crime = null;
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM crime WHERE year = ?");
        stm.setInt(1, year);
        ResultSet results = stm.executeQuery();
        while (results.next()) {
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
                "/",
                ((request, response) -> {
                    ArrayList<Crime> crime = selectAll(con);
                    JsonSerializer serializer = new JsonSerializer();
                    System.out.println(selectAll(con));
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

                    if (username.isEmpty() || password.isEmpty() || ip.equals(selectIP(con, user.ip).toString())) {
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
                        if (ip.matches(selectIP(con, ip).toString())) {
                            Spark.halt(403);
                        }
                        insertUser(con, user);
                    } else if (!password.equals(user.password) || (user.access = false)) {
                        Spark.halt(403);
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";
                })
        );


        // Method for banning a user.

        // Method for loading forum entries.


        // Method for posting forum entries.


        // Method for updating forum rating.



    }

}
