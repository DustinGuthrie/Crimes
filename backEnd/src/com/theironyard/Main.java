package com.theironyard;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Agronis on 11/5/15.
 */
public class Main {

    public static void createTables(Connection con) throws SQLException {
        Statement stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS crime (id IDENTITY, abbrev VARCHAR, name VARCHAR,, year INT, population INT," +
                "total INT, murder INT, manslaughter INT, rape INT, robbery INT, assault INT)");
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
    public static ArrayList<Crime> selectStateCrimes(Connection conn, String name) throws SQLException {
        PreparedStatement stm = conn.prepareStatement("SELECT * FROM crime WHERE name = ?");
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
            crime.manslaughter = results.getInt("manslaughter");
            crime.rape = results.getInt("rape");
            crime.robbery = results.getInt("robbery");
            crime.assault = results.getInt("assault");
            crimes.add(crime);
        }
        return crimes;
    }

    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:h2:./main");
        createTables(con);
    }

}
