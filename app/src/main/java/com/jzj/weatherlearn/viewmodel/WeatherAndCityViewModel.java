package com.jzj.weatherlearn.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.SharedPreferencesManager;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.CityAndWeather;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.tool.ApiUtil;
import com.jzj.weatherlearn.tool.DataUtil;
import com.jzj.weatherlearn.tool.GsonUtil;
import com.jzj.weatherlearn.tool.SkyconUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherAndCityViewModel extends AndroidViewModel {

    private String TAG = WeatherAndCityViewModel.class.getName();
    MutableLiveData<CityAndWeather> cityAndWeatherLiveData;
    Context context;
    SharedPreferences sharedPreferences;

    public WeatherAndCityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        sharedPreferences = SharedPreferencesManager.getSharedPreferences(TAG);
        cityAndWeatherLiveData = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        SharedPreferencesManager.itemDestroy(TAG);
    }

    /**
     * 请求天气预报API
     * 传入city对象获取经纬度
     */
    private void sendWeatherRequest(City city, Callback callback) {
        String buffer = city.getLon() + "," + city.getLat();
        sendWeatherRequest(buffer, callback);
    }

    /**
     * 请求天气预报API
     * 传入经纬度数值
     */
    private void sendWeatherRequest(double longitude, double latitude, Callback callback) {
        String buffer = longitude + "," + latitude;
        sendWeatherRequest(buffer, callback);
    }

    /**
     * 请求天气预报API
     * 传入经纬度字符串
     */
    private void sendWeatherRequest(String LongitudeAndLatitude, Callback callback) {
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

    /**
     * 获取天气信息
     */
    public void getWeatherInfo(City city) {
        final Weather[] weather = new Weather[1];
        //使用城市代号作为键值
        String weatherCacheByCityStr = DataUtil.readSharedPreferences(String.valueOf(city.getCityCode()), sharedPreferences);
        if (weatherCacheByCityStr == null) {
            //网络请求天气信息
            sendWeatherRequest(city, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    displayToast("请求失败");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseStr = response.body().string();
                    //写入缓存
                    if (responseStr != null) {
                        DataUtil.writeSharedPreferences(String.valueOf(city.getCityCode()), responseStr, sharedPreferences);
                        weather[0] = GsonUtil.handlerWeatherJson(responseStr);
                        CityAndWeather cityAndWeather = new CityAndWeather(city);
                        cityAndWeather.setWeather(weather[0]);
                        cityAndWeatherLiveData.postValue(cityAndWeather);
                    }
                }
            });
        } else {
            weather[0] = GsonUtil.handlerWeatherJson(weatherCacheByCityStr);
            if (weather[0] == null) {
                displayToast("weather not info");
            } else {
                CityAndWeather cityAndWeather = new CityAndWeather(city);
                cityAndWeather.setWeather(weather[0]);
                cityAndWeatherLiveData.postValue(cityAndWeather);
            }
        }
    }

    /**
     * 删除缓存重新从网络获取天气信息
     */
    public void getWeatherInfoFromNetwork(City city) {
        sharedPreferences.edit().remove(String.valueOf(city.getCityCode())).apply();
        getWeatherInfo(city);
    }

    /**
     * 获取生活建议
     *
     * @param weather
     * @return
     */
    public String getWeatherAdvice(Weather weather) {
        String ln = "\r\n";
        StringBuilder advice = new StringBuilder();
        List<String> adviceList = new ArrayList<>();
        //温度提醒
        if ((weather.result.daily.temperature.get(0).avg < 20)) {
            adviceList.add("天冷请注意添衣,今日平均温度" + weather.result.daily.temperature.get(0).avg + ln);
        }
        //下雨提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.LIGHT_RAIN) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.MODERATE_RAIN)) {
            adviceList.add("今日有雨,出门记得备伞" + ln);
        }
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.HEAVY_RAIN) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.STORM_RAIN)) {
            adviceList.add("今日雨较大,尽量避免出行" + ln);
        }
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.HEAVY_HAZE)) {
            adviceList.add("今日雾霾较重,出门请记得备口罩" + ln);
        }
        //下雪提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.HEAVY_SNOW) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.STORM_SNOW)) {
            adviceList.add("今日雪厚,尽量避免出门" + ln);
        }
        //雾提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.FOG)) {
            adviceList.add("今日有雾,出行请注意安全" + ln);
        }
        //大风提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.WIND)) {
            adviceList.add("今日风较大,出行请注意安全" + ln);
        }
        //阴天提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.CLOUDY) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.PARTLY_CLOUDY_NIGHT) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.PARTLY_CLOUDY_DAY)) {
            adviceList.add("今日多云, 出行请做好应对雨天的准备, 适合出门, 祝今天好心情" + ln);
        }
        //晴天建议
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.CLEAR_DAY) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.CLEAR_NIGHT)) {
            adviceList.add("今日晴, 出行注意防晒, 适合出门, 祝今天好心情" + ln);
        }
        for (int i = 0; i < adviceList.size(); i++) {
            advice.append((i + 1) + ".  " + adviceList.get(i));
        }
        return advice.toString();
    }

    /**
     * 天气现象图标映射
     */
    public Drawable handlerWeatherIcno(String skyconVal) {
        Drawable drawable = null;
        switch (skyconVal) {
            //晴天
            case SkyconUtil.CLEAR_DAY:
            case SkyconUtil.CLEAR_NIGHT:
                drawable = context.getResources().getDrawable(R.drawable.ic_clear_day, null);
                break;
            //多云
            case SkyconUtil.PARTLY_CLOUDY_DAY:
            case SkyconUtil.PARTLY_CLOUDY_NIGHT:
            //阴天
            case SkyconUtil.CLOUDY:
                drawable = context.getResources().getDrawable(R.drawable.ic_partly_cloudy_day, null);
                break;
            //雨天
            case SkyconUtil.LIGHT_RAIN:
                drawable = context.getResources().getDrawable(R.drawable.ic_light_rain, null);
                break;
            case SkyconUtil.MODERATE_RAIN:
                drawable = context.getResources().getDrawable(R.drawable.ic_moderate_rain, null);
                break;
            case SkyconUtil.HEAVY_RAIN:
            case SkyconUtil.STORM_RAIN:
                drawable = context.getResources().getDrawable(R.drawable.ic_heavy_rain, null);
                break;
            //雾
            case SkyconUtil.FOG:
                drawable = context.getResources().getDrawable(R.drawable.ic_fog, null);
                break;
            //雪
            case SkyconUtil.LIGHT_SNOW:
                drawable = context.getResources().getDrawable(R.drawable.ic_light_snow, null);
                break;
            case SkyconUtil.MODERATE_SNOW:
                drawable = context.getResources().getDrawable(R.drawable.ic_moderate_snow, null);
                break;
            case SkyconUtil.HEAVY_SNOW:
            case SkyconUtil.STORM_SNOW:
                drawable = context.getResources().getDrawable(R.drawable.ic_heavy_snow, null);
                break;
            //尘
            case SkyconUtil.DUST:
            case SkyconUtil.SAND:
                drawable = context.getResources().getDrawable(R.drawable.ic_dust_with_wind, null);
                break;
            //风
            case SkyconUtil.WIND:
                drawable = context.getResources().getDrawable(R.drawable.ic_wind, null);
                break;
            default:
                drawable = context.getResources().getDrawable(R.drawable.ic_partly_cloudy_day, null);
                break;
        }
        return drawable;
    }

    /**
     * Toast
     *
     * @param message
     */
    private void displayToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public MutableLiveData<CityAndWeather> getCityAndWeatherLiveData() {
        return cityAndWeatherLiveData;
    }
}
