package com.theironyard;
import jodd.json.JsonSerializer;
import spark.Spark;

import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Agronis on 11/5/15.
 */
public class Main {

    public static void createTables(Connection con) throws SQLException {
        Statement stm = con.createStatement();
        stm.execute("CREATE TABLE IF NOT EXISTS crime (id IDENTITY, abbrev VARCHAR, name VARCHAR, year INT, population INT," +
                "total INT, murder INT, rape INT, robbery INT, assault INT, forum INT)");
    }


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

    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:h2:./main");
        createTables(con);
        populateDatabase(con);

        Spark.get(
                "/",
                ((request, response) -> {
                    ArrayList<Crime> crime = selectAll(con);
                    JsonSerializer serializer = new JsonSerializer();
                    System.out.println(selectAll(con));
                    return serializer.serialize(crime);
                })
        );

    }

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

}
