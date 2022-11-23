package com.jzj.weatherlearn.ui;

import com.jzj.weatherlearn.model.Weather;

public interface ActivityCallback {

    void handleWeatherBg(Weather weather, int messageWhat);

}
