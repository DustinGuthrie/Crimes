package com.theironyard;
/**
 * Created by Agronis on 11/5/15.
 */
public class Crime {
    int id;
    String name;
    String abbrev;
    int year;
    int population;
    int total;
    int murder;
    int rape;
    int robbery;
    int assault;
    String user;

    public Crime() {

    }

    public Crime(int id, String name, String abbrev, int year, int population, int total, int murder, int rape, int robbery, int assault) {

        this.id = id;
        this.name = name;
        this.abbrev = abbrev;
        this.year = year;
        this.population = population;
        this.total = total;
        this.murder = murder;
        this.rape = rape;
        this.robbery = robbery;
        this.assault = assault;
    }

    public Crime(String name, String abbrev, int year, int population, int total, int murder, int rape, int robbery, int assault) {

        this.name = name;
        this.abbrev = abbrev;
        this.year = year;
        this.population = population;
        this.total = total;
        this.murder = murder;
        this.rape = rape;
        this.robbery = robbery;
        this.assault = assault;
    }
    public String getName() {

        return name;
    }
    public String getAbbrev() {

        return abbrev;
    }
    public int getYear() {

        return year;
    }
    public int getPopulation() {

        return population;
    }
    public int getTotal() {

        return total;
    }
    public int getMurder() {

        return murder;
    }
    public int getRape() {

        return rape;
    }
    public int getRobbery() {

        return robbery;
    }
    public int getAssault() {

        return assault;
    }

}