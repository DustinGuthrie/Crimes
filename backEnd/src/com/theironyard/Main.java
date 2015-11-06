package com.theironyard;
import sun.plugin2.message.ProxyReplyMessage;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Agronis on 11/5/15.
 */
public class Main {

    public static void createTables(Connection con) throws SQLException {
        Statement stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS user (id IDENTITY, password VARCHAR, admin BOOLEAN, ip VARCHAR," +
                " access BOOLEAN, picture VARCHAR)");
        stm.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, reply_id INT, username VARCHAR, " +
                "text VARCHAR, rating INT");
        stm.execute("CREATE TABLE IF NOT EXISTS crime (id IDENTITY, abbrev VARCHAR, name VARCHAR,, year INT, population INT," +
                "total INT, murder INT, manslaughter INT, rape INT, robbery INT, assault INT)");
    }

    public static void insertUser(Connection con, String password, boolean admin, String ip, boolean access, String picture) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO users (NULL, ?, ?, ?, ?, ?)");
        stm.setString(1, password);
        stm.setBoolean(2, admin);
        stm.setString(3, ip);
        stm.setBoolean(4, access);
        stm.setString(5, picture);
        stm.execute();

    }

    public static void insertCrime(Connection con, Crime c) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO crime VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        stm.setString(1, c.name);
        stm.setString(2, c.abbrev);
        stm.setInt(3, c.year);
        stm.setInt(4, c.population);
        stm.setInt(5, c.total);
        stm.setInt(6, c.murder);
        stm.setInt(7, c.manslaughter);
        stm.setInt(8, c.rape);
        stm.setInt(9, c.robbery);
        stm.setInt(10, c.assault);
        stm.execute();

    }
    public static Crime selectYear(Connection conn, int year, String name) throws SQLException {
        Crime crime = null;
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM crime WHERE year = ?, name = ?");
        stm.setInt(1, year);
        stm.setString(2, name);
        ResultSet results = stm.executeQuery();
        if(results.next()){
            crime = new Crime();
            crime.year = results.getInt("year");
            crime.name = results.getString("name");
            crime.abbrev = results.getString("abbrev");
            crime.population = results.getInt("population");
            crime.total = results.getInt("total");
            crime.murder = results.getInt("murder");
            crime.manslaughter = results.getInt("manslaughter");
            crime.rape = results.getInt("rape");
            crime.robbery = results.getInt("robbery");
            crime.assault = results.getInt("assault");
        }
        return crime;
    }

    public static ArrayList<Crime> selectStateCrimes(Connection con, String name) throws SQLException {
        PreparedStatement stm = con.prepareStatement("SELECT * FROM crime WHERE name = ?");
        stm.setString(1, name);
        ArrayList<Crime> crimes = new ArrayList();
        ResultSet results = stm.executeQuery();
        while(results.next()){
            Crime crime = new Crime();
            crime.abbrev = results.getString("abbrev");
            crime.year = results.getInt("year");
            crime.population = results.getInt("population");
            crime.total = results.getInt("total");
            crime.murder = results.getInt("murder");
            crime.manslaughter = results.getInt("manslaughter");
            crime.rape = results.getInt("rape");
            crime.robbery = results.getInt("robbery");
            crime.assault = results.getInt("assault");
            crimes.add(crime);
        }
        return crimes;
    }

    public static void insertMessage(Connection con, int id, int replyId, String username, String text, int rating) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?, ?, ?, ?)");
        stm.setInt(1, id);
        stm.setInt(2, replyId);
        stm.setString(3, username);
        stm.setString(4, text);
        stm.setInt(5, rating);
        stm.execute();
    }


    public static Message selectMessage(Connection con, int id) throws SQLException {
        Message message = null;
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM messages INNER JOIN users ON messages.user_id = users.id " +
                "WHERE messages.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            message = new Message();
            message.id = results.getInt("messages.id");
            message.replyId = results.getInt("messages.reply_id");
            message.username = results.getString("users.name");
            message.text = results.getString("messages.text");
            message.rating = results.getInt("message.rating");
        }
        return message;
    }

    public static ArrayList<Message> selectReplies(Connection conn, int replyId) throws SQLException {
        ArrayList<Message> replies = new ArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages INNER JOIN users ON messages.user_id = users.id " +
                "WHERE messages.reply_id = ?");
        stmt.setInt(1, replyId);
        ResultSet results = stmt.executeQuery();
        while(results.next()){
            Message message = new Message();
            message.id = results.getInt("messages.id");
            message.replyId = results.getInt("messages.reply_id");
            message.username = results.getString("users.name");
            message.text = results.getString("messages.text");
            message.rating = results.getInt("messages.rating");
            replies.add(message);
        }
        return replies;
    }

    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:h2:./main");
        createTables(con);
    }

}
