package com.example.weathergarden.weather;

public class LocationData {
    public String x;
    public String y;
    public String nx;
    public String ny;

    public LocationData(Double x, Double y, int nx, int ny){
        this.x = String.valueOf(x);
        this.y = String.valueOf(y);
        this.nx = String.valueOf(nx);
        this.ny = String.valueOf(ny);
    }
}
