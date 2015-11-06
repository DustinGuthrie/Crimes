package com.theironyard;
import org.junit.Test;

import java.sql.*;
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
        stm.execute("DROP TABLE people");
        con.close();
    }

    @Test
    public void populate() throws SQLException {
        Connection con = startConnection();
        Main.populateDatabase(con);
        PreparedStatement stm = con.prepareStatement("SELECT * FROM people");
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
    
}