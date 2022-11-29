package com.jzj.weatherlearn.model;

public class WeatherAndCityModel {

    private Weather weather;
    private City city;

    public WeatherAndCityModel(City city) {
        this.city = city;
    }

    public WeatherAndCityModel(Weather weather, City city) {
        this.weather = weather;
        this.city = city;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
