package com.jzj.weatherlearn.util;

import com.jzj.weatherlearn.model.City;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NetworkUtil {

    /**
     * 请求天气预报API
     * 传入city对象获取经纬度
     */
    public static void sendWeatherRequest(City city, Callback callback) {
        String buffer = city.getLon() + "," + city.getLat();
        sendWeatherRequest(buffer, callback);
    }

    /**
     * 请求天气预报API
     * 传入经纬度数值
     */
    public static void sendWeatherRequest(double longitude, double latitude, Callback callback) {
        String buffer = longitude + "," + latitude;
        sendWeatherRequest(buffer, callback);
    }

    /**
     * 请求天气预报API
     * 传入经纬度字符串
     */
    public static void sendWeatherRequest(String LongitudeAndLatitude, Callback callback) {
        String url = ApiUtil.WEATHER_API_URL.replace(ApiUtil.REPLACE_STR, LongitudeAndLatitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(callback);
            }
        }).start();
    }

}
