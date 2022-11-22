package com.example.weathergarden.garden;

public class PlantParameters {
    public String name;
    public int level;
    public double growPoint;
    public double growLimit;
    public float water;
    public int waterLimit;
    public int nutrient;
    public int nutrientLimit;
    public int wither;
    public int witherLimit;
    public int time;
    public String temp;
    public String hum;
    public String info;

    public PlantParameters() {
        name = "";
        level = 0;
        growPoint = 0;
        growLimit = 0;
        water = 0;
        waterLimit = 0;
        nutrient = 0;
        nutrientLimit = 0;
        wither = 0;
        witherLimit = 0;
        time = 0;
        temp = "";
        hum = "";
        info = "";
    }
}
