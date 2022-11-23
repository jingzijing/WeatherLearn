package com.jzj.weatherlearn.model.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 主要天气信息
 */
public class Result {

    public Daily daily;

    public Integer primary;

    public class Daily {

        public String status;

        public List<Astro> astro;

        @SerializedName("precipitation_08h_20h")
        public List<Precipitation> precipitationDay;

        @SerializedName("precipitation_20h_32h")
        public List<Precipitation> precipitationNight;

        public List<Precipitation> precipitation;

        public List<Temperature> temperature;

        @SerializedName("temperature_08h_20h")
        public List<Temperature> temperatureDay;

        @SerializedName("temperature_20h_32h")
        public List<Temperature> temperatureNight;

        public List<Wind> wind;

        @SerializedName("wind_08h_20h")
        public List<Wind> windDay;

        @SerializedName("wind_20h_32h")
        public List<Wind> windNight;

        public List<Humidity> humidity;

        public List<Cloudrate> cloudrate;

        public List<Pressure> pressures;

        public List<Visibility> visibility;

        public List<Dswrf> dswrf;

        @SerializedName("air_quality")
        public AirQuality airQuality;

        public List<Skycon> skycon;

        @SerializedName("skycon_08h_20h")
        public List<Skycon> skyconDay;

        @SerializedName("skycon_20h_32h")
        public List<Skycon> skyconNight;

        @SerializedName("life_index")
        public LifeIndex lifeIndex;

        @Override
        public String toString() {
            return "Daily{" +
                    "status='" + status + '\'' +
                    ", astro=" + astro +
                    ", precipitationDay=" + precipitationDay +
                    ", precipitationNight=" + precipitationNight +
                    ", precipitation=" + precipitation +
                    ", temperature=" + temperature +
                    ", temperatureDay=" + temperatureDay +
                    ", temperatureNight=" + temperatureNight +
                    ", wind=" + wind +
                    ", windDay=" + windDay +
                    ", windNight=" + windNight +
                    ", humidity=" + humidity +
                    ", cloudrate=" + cloudrate +
                    ", pressures=" + pressures +
                    ", visibility=" + visibility +
                    ", dswrf=" + dswrf +
                    ", airQuality=" + airQuality +
                    ", skycon=" + skycon +
                    ", skyconDay=" + skyconDay +
                    ", skyconNight=" + skyconNight +
                    ", lifeIndex=" + lifeIndex +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "daily=" + daily +
                ", primary=" + primary +
                '}';
    }
}
