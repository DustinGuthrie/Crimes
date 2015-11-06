package com.theironyard;
/**
 * Created by Agronis on 11/5/15.
 */
public class Crime {

    String name;
    String abbrev;
    int year;
    int population;
    int total;
    int murder;
    int manslaughter;
    int rape;
    int robbery;
    int assault;

    public Crime() {}

    public Crime(String name, String abbrev, int year, int population, int total, int murder, int manslaughter, int rape, int robbery, int assault) {

        this.name = name;
        this.abbrev = abbrev;
        this.year = year;
        this.population = population;
        this.total = total;
        this.murder = murder;
        this.manslaughter = manslaughter;
        this.rape = rape;
        this.robbery = robbery;
        this.assault = assault;
    }
}