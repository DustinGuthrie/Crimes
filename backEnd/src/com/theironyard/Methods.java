package com.theironyard;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Agronis on 11/8/15.
 */
public class Methods {

    // SQL Table Creation
    public static void createTables(Connection con) throws SQLException {
        Statement stm = con.createStatement();
        stm.execute("DROP TABLE crime");
        stm.execute("DROP TABLE users");
        stm.execute("DROP TABLE messages");
        stm.execute("CREATE TABLE IF NOT EXISTS crime (id IDENTITY, abbrev VARCHAR, name VARCHAR, year INT, population INT," +
                "total INT, murder INT, rape INT, robbery INT, assault INT)");
        stm.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, password VARCHAR, postCount INT, admin BOOLEAN, ip VARCHAR, access BOOLEAN)");
        stm.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, userId INT, crimeId INT, msgId INT, rating INT, text VARCHAR, timestamp TIMESTAMP)");
        stm.execute("CREATE TABLE IF NOT EXISTS msgs (id IDENTITY, username VARCHAR, timestamp TIMESTAMP, text VARCHAR)");
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

    // TEST PASSED
    // Method to ban user.
    public static void banUser(Connection con, String username) throws SQLException {
        PreparedStatement stm = con.prepareStatement("UPDATE users SET access = false WHERE username = ?");
        stm.setString(1, username);
        stm.executeUpdate();
    }


    // Method for inserting a new message to a crime object.
    public static void insertMsg(Connection con, int userId, int crimeId, int msgId, String text, int rating, LocalDateTime timestamp) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?, ?, ?, ?, ?)");
        stm.setInt(1, userId);
        stm.setInt(2, crimeId);
        stm.setInt(3, msgId);
        stm.setInt(4, rating);
        stm.setString(5, text);
        stm.setTimestamp(6, Timestamp.valueOf(timestamp));
        stm.execute();
    }

    // Selects all messages associated to the crime record.
    public static ArrayList<Message> selectMsgs(Connection con, int crimeId) throws SQLException {

        ArrayList<Message> selectMsgs = new ArrayList<>();
        PreparedStatement stm = con.prepareStatement(
                "SELECT * FROM messages WHERE messages.crimeId = ?");
        stm.setInt(1, crimeId);
        ResultSet results = stm.executeQuery();
        while (results.next()) {
            Message message = new Message();
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
        PreparedStatement stm = con.prepareStatement("SELECT * FROM messages WHERE messages.msgId = ?");
        stm.setInt(1, id);
        ResultSet results = stm.executeQuery();
        if (results.next()) {
            message = new Message();
            message.userId = results.getInt("userId");
            message.crimeId = results.getInt("crimeId");
            message.msgId = results.getInt("msgId");
            message.text = results.getString("text");
            message.timestamp = results.getTimestamp("timestamp").toLocalDateTime();
            message.rating = results.getInt("rating");
        }
        return message;
    }

    // TEST PASSED
    // Method for deleting a message.
    public static void deleteMsg(Connection con, int id) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("DELETE FROM messages WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    // Method for an Admin deleting a message.
    public static void adminDeleteMsg(Connection con, Message m) throws SQLException {
        PreparedStatement stm = con.prepareStatement(
                "UPDATE message SET text = ?, rating = ?, userId = ?, timestamp = ? WHERE id = ?");
        stm.setString(1, m.text);
        stm.setInt(2, m.rating);
        stm.setInt(3, m.userId);
        stm.setTimestamp(4, Timestamp.valueOf(m.timestamp));
        stm.setInt(5, m.id);
        stm.executeUpdate();
    }

    // TEST PASSED
    // Method for editing a message & it's rating.  Keep's new time.
    public static void editMsg(Connection con, Message m) throws SQLException {
        PreparedStatement stm = con.prepareStatement(
                "UPDATE messages SET text = ?, timestamp = ? WHERE msgId = ?");
        stm.setString(1, m.text);
        stm.setTimestamp(2, Timestamp.valueOf(m.timestamp));
        stm.setInt(3, m.msgId);

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
            crimes.add(crime);
        }
        return crimes;
    }

    // Selecting crimes strictly for one year from one state.
    public static Crime selectSingle(Connection conn, int year, String name) throws SQLException {
        Crime crime = null;
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM crime WHERE year = ? AND name = ?");
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
            crimes.add(crime);
        }
        return crimes;
    }

    // Inserting into faux-message board.
    public static void insMsg(Connection con, Message m, User u) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO msgs VALUES (NULL, ?, ?, ?)");
        stm.setString(1, m.username);
        stm.setTimestamp(2, Timestamp.valueOf(m.timestamp));
        stm.setString(3, m.text);
        stm.execute();
    }

    // Getting msgs for faux-message board.
    public static ArrayList<Message> getMsg(Connection con) throws SQLException {
        PreparedStatement stm = con.prepareStatement("SELECT * FROM msgs");
        ArrayList<Message> msgs = new ArrayList();
        ResultSet results = stm.executeQuery();
        while (results.next()) {
            Message m = new Message();
            m.username = results.getString("username");
            m.timestamp = results.getTimestamp("timestamp").toLocalDateTime();
            m.text = results.getString("text");
            msgs.add(m);
        }
        return msgs;
    }
}
