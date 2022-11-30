package com.jzj.weatherlearn.model;

import com.google.gson.annotations.SerializedName;
import com.jzj.weatherlearn.model.gson_weather.Result;
import com.jzj.weatherlearn.model.gson_weather.Skycon;
import com.jzj.weatherlearn.model.gson_weather.Temperature;

import java.util.ArrayList;
import java.util.List;

/**
 * 彩云天级别预报api，Json解析类
 */
public class Weather {

    public String status;

    @SerializedName("api_version")
    public String apiVersion;

    @SerializedName("api_status")
    public String apiStatus;

    public String lang;

    public String unit;

    public Integer tzshift;

    public String timezone;

    @SerializedName("server_time")
    public Long serverTime;

    public List<Float> location;

    public Result result;

    public List<WeatherListModel> getResultDailyWeatherModel() {
        ArrayList<WeatherListModel> weatherList = new ArrayList<>();
        int largeInt = Math.max(result.daily.temperature.size(), result.daily.skycon.size());
        for (int i = 0; i < largeInt; i++) {
            Temperature temperature = null;
            Skycon skycon = null;
            if (i < result.daily.temperature.size()) {
                temperature = result.daily.temperature.get(i);
            }
            if (i < result.daily.skycon.size()) {
                skycon = result.daily.skycon.get(i);
            }
            weatherList.add(new WeatherListModel(temperature, skycon));
        }
        return weatherList;
    }

}
