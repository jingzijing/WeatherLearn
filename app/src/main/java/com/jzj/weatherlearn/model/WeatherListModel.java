package com.jzj.weatherlearn.model;

import com.jzj.weatherlearn.model.gson.Skycon;
import com.jzj.weatherlearn.model.gson.Temperature;

public class WeatherListModel {

    public Temperature temperature;

    public Skycon skycon;

    public WeatherListModel(Temperature temperature, Skycon skycon) {
        this.temperature = temperature;
        this.skycon = skycon;
    }
}
