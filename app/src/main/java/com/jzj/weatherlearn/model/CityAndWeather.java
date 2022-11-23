package com.jzj.weatherlearn.model;

public class CityAndWeather {

    public Weather weather;

    public City city;

    public CityAndWeather(City city) {
        this.city = city;
    }

    public CityAndWeather(Weather weather, City city) {
        this.weather = weather;
        this.city = city;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
