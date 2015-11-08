package com.theironyard;
import org.junit.Test;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Agronis on 11/6/15.
 */
public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(con);
        return con;
    }

    public void endConnection(Connection con) throws SQLException {

        Statement stm = con.createStatement();
        stm.execute("DROP TABLE crime");
        con.close();
    }

    @Test
    public void populate() throws SQLException {
        Connection con = startConnection();
        Main.populateDatabase(con);
        PreparedStatement stm = con.prepareStatement("SELECT * FROM crime");
        ArrayList<Crime> crimes = new ArrayList<>();
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
        endConnection(con);
    }

    @Test
    public void selectCrime() throws SQLException {
        Connection con = startConnection();
        Main.createTables(con);
        Crime p = new Crime("AK","Alaska",2008,686293,4475,27,447,645,3356, 0);
        Crime q = new Crime("AK","Alaska",2008,686293,4475,27,447,645,3356, 0);
        Main.insertCrime(con, p);
        Main.insertCrime(con, q);
        ArrayList<Crime> crimes = Main.selectAll(con);
        endConnection(con);

        assertTrue(crimes.size() == 2);

    }

    @Test
    public void selectUser() throws SQLException {
        Connection con = startConnection();
        Main.createTables(con);
        User user = new User("Test", "test", "24.24.24.24");
        System.out.println(user);
        Main.insertUser(con, user);
        Main.selectUser(con, "Test");
        User u = Main.selectUser(con, "Test");
        endConnection(con);

        assertTrue(u != null);
    }

    @Test
    public void selectMsgs() throws SQLException {
        Connection con = startConnection();
        Main.createTables(con);
        Crime c = new Crime("T", "Test", 1, 1, 1, 1, 1, 1, 1);
        User u = new User("Test", "testpass", "24.24.24.24");
        Message m = new Message(u.id, c.id, 1, "test", 1, LocalDateTime.now());

        Main.insertMsg(con, m, u, c);
        ArrayList<Message> test = Main.selectMsgs(con, 1);
        endConnection(con);

        assertTrue(test.size() == 1);
    }

    int id;
    int userId;
    int crimeId;
    int msgId;
    String text;
    int rating;
    LocalDateTime timestamp;
    
}