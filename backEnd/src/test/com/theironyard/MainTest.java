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
        stm.execute("DROP TABLE users");
        stm.execute("DROP TABLE messages");
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
            crimes.add(crime);
        }
        endConnection(con);
    }

    @Test
    public void insertCrime() throws SQLException {
        Connection con = startConnection();
        Crime c = new Crime();
        c.id = 1;
        c.name = "Arizona";
        c.abbrev = "AZ";
        c.year = 1990;
        c.population = 19000;
        c.total = 23000;
        c.murder = 21000;
        c.rape = 5;
        c.robbery = 10;
        c.assault = 12;
        Main.insertCrime(con, c);
        Crime test = Main.selectSingle(con, 1990, "Arizona");
        endConnection(con);
        assertTrue(test.name != null);
    }

    @Test
    public void selectCrime() throws SQLException {
        Connection con = startConnection();
        Crime p = new Crime(1, "AK","Alaska",2008,686293,4475,27,447,645,3356);
        Crime q = new Crime(1, "AK","Alaska",2008,686293,4475,27,447,645,3356);
        Main.insertCrime(con, p);
        Main.insertCrime(con, q);
        ArrayList<Crime> crimes = Main.selectAll(con);
        endConnection(con);

        assertTrue(crimes.size() == 2);

    }

    @Test
    public void insertUser() throws SQLException {
        Connection con = startConnection();
        User u = new User();
        u.username = "Matt";
        u.password = "123";
        u.ip = "2";
        Main.insertUser(con, u);
        User test = Main.selectUser(con, "Matt");
        endConnection(con);

        assertTrue(test.username == "Matt");
    }

    @Test
    public void selectUser() throws SQLException {
        Connection con = startConnection();
        User user = new User("Test", "test", "24.24.24.24");
        System.out.println(user);
        Main.insertUser(con, user);
        User u = Main.selectUser(con, "Test");
        endConnection(con);

        assertTrue(u != null);
    }

    @Test
    public void insertMsg() throws SQLException {
        Connection con = startConnection();
        User u= new User ("Matt", "123", "ip");
        Main.insertUser(con, u);
        LocalDateTime time = LocalDateTime.now();
        Crime c = new Crime();
        Main.insertCrime(con, c);
        Main.insertMsg(con, 1, 1, 1, "Rekt by Java", 1, time);
        Message message = Main.selectMsg(con, 1);
        endConnection(con);

        assertTrue(message != null);
    }

    @Test
    public void selectMsg() throws SQLException {
        Connection con = startConnection();
        LocalDateTime time = LocalDateTime.now();
        Message message = new Message();
        System.out.println(message);
        Main.insertMsg(con, 1, 1, 1, "Hello", 1, time);
        Message m = Main.selectMsg(con, 1);
        endConnection(con);

        assertTrue(m != null);
    }

    @Test
    public void selectMsgs() throws SQLException {
        Connection con = startConnection();
        LocalDateTime time = LocalDateTime.now();
        Main.insertMsg(con, 1, 1, 1, "Hello", 1, time);
        Main.insertMsg(con, 2, 1, 2, "Hi", 2, time);
        ArrayList<Message> messages = Main.selectMsgs(con, 1);
        endConnection(con);
        assertTrue(messages.size() == 2);

    }
}