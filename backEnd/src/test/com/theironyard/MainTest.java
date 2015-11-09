package com.theironyard;
import org.junit.Test;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Agronis on 11/6/15.
 */
public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:h2:./test");
        Methods.createTables(con);
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
        Methods.populateDatabase(con);
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
        Methods.insertCrime(con, c);
        Crime test = Methods.selectSingle(con, 1990, "Arizona");
        endConnection(con);
        assertTrue(test.name != null);
    }

    @Test
    public void selectCrime() throws SQLException {
        Connection con = startConnection();
        Crime p = new Crime(1, "AK","Alaska",2008,686293,4475,27,447,645,3356);
        Crime q = new Crime(1, "AK","Alaska",2008,686293,4475,27,447,645,3356);
        Methods.insertCrime(con, p);
        Methods.insertCrime(con, q);
        ArrayList<Crime> crimes = Methods.selectAll(con);
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
        Methods.insertUser(con, u);
        User test = Methods.selectUser(con, "Matt");
        endConnection(con);

        assertTrue(test.username == "Matt");
    }

    @Test
    public void selectUser() throws SQLException {
        Connection con = startConnection();
        User user = new User("Test", "test", "24.24.24.24");
        System.out.println(user);
        Methods.insertUser(con, user);
        User u = Methods.selectUser(con, "Test");
        endConnection(con);

        assertTrue(u != null);
    }

    @Test
    public void banUser() throws SQLException {
        Connection con = startConnection();
        Methods.createTables(con);
        User u = new User();
        u.username = "Matt";
        u.password = "123";
        u.ip = "2";
        Methods.insertUser(con, u);
        Methods.banUser(con, "Matt");
        User test = Methods.selectUser(con, "Matt");
        endConnection(con);

        assertTrue(!test.access);
    }

    @Test
    public void insertMsg() throws SQLException {
        Connection con = startConnection();
        User u= new User ("Matt", "123", "ip");
        Methods.insertUser(con, u);
        LocalDateTime time = LocalDateTime.now();
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
        Methods.insertCrime(con, c);
        Methods.insertMsg(con, 1, 1, 1, "Rekt by Java", 1, time);
        Message message = Methods.selectMsg(con, 1);
        endConnection(con);

        assertTrue(message != null);
    }

    @Test
    public void editMsg() throws SQLException {
        Connection con = startConnection();
        Methods.createTables(con);
        LocalDateTime time = LocalDateTime.now();
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
        Methods.insertCrime(con, c);
        Crime test = Methods.selectSingle(con, 1990, "Arizona");
        User u = new User();
        u.username = "Matt";
        u.password = "123";
        u.ip = "2";
        u.id = 1;
        Methods.insertUser(con, u);
        Methods.insertMsg(con, 1, 1, 1, "Rekt by Java", 1, time);
        Message m = Methods.selectMsg(con, 1);
        m.text = "TEEEST";
        m.timestamp = LocalDateTime.now();
        Methods.editMsg(con, m);
        Message me = Methods.selectMsg(con, 1);
        endConnection(con);

        assertTrue(me.text.equals("TEEEST"));


    }

    @Test
    public void deleteMsg() throws SQLException {
        Connection con = startConnection();
        Methods.createTables(con);
        User u= new User ("Matt", "123", "ip");
        Methods.insertUser(con, u);
        LocalDateTime time = LocalDateTime.now();
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
        Methods.insertCrime(con, c);
        Methods.insertMsg(con, 1, 1, 1, "Rekt by Java", 1, time);
        Methods.insertMsg(con, 2, 1, 1, "Blah", 1, time);
        Methods.deleteMsg(con, 2);
        ArrayList<Message> message = Methods.selectMsgs(con, 1);
        endConnection(con);

        assertTrue(message.size() == 1);
    }


}