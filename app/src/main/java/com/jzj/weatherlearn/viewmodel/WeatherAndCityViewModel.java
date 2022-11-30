package com.jzj.weatherlearn.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.model.WeatherAndCityModel;
import com.jzj.weatherlearn.tool.ApiUtil;
import com.jzj.weatherlearn.tool.DataUtil;
import com.jzj.weatherlearn.tool.GsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherAndCityViewModel extends AndroidViewModel {

    MutableLiveData<List<WeatherAndCityModel>> livedata;
    Context context;

    public WeatherAndCityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        /**
         * livedata初始化
         */
        livedata = new MutableLiveData<>();
        List<WeatherAndCityModel> models = new ArrayList<>();
        int size = CitySetting.getInstance().getCachesCitiesSize();
        for (int i = 0; i < size; i++) {
            models.add(new WeatherAndCityModel(CitySetting.getInstance().getCacheCities().get(i)));
        }
        livedata.setValue(models);
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
        String weatherCacheByCityStr = DataUtil.readSharedPreferences(String.valueOf(city.getCityCode()), App.sharedPreferences);
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
                        DataUtil.writeSharedPreferences(String.valueOf(city.getCityCode()), responseStr, App.sharedPreferences);
                        weather[0] = GsonUtil.handlerWeatherJson(responseStr);
                        updateWeatherInfoByCityCode(city.getCityCode(), weather[0]);
                    }
                }
            });
        } else {
            weather[0] = GsonUtil.handlerWeatherJson(weatherCacheByCityStr);
            if (weather[0] == null) {
                displayToast("weather not info");
            } else {
                updateWeatherInfoByCityCode(city.getCityCode(), weather[0]);
            }
        }
    }

    /**
     * 删除缓存重新从网络获取天气信息
     */
    public void getWeatherInfoFromNetwork(City city) {
        App.sharedPreferences.edit()
                .remove(String.valueOf(city.getCityCode()))
                .apply();
        getWeatherInfo(city);
    }

    /**
     * Toast
     */
    private void displayToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 返回livedata
     */
    public MutableLiveData<List<WeatherAndCityModel>> getLiveData() {
        return livedata;
    }

    /**
     * 更新livedata的weather信息
     *
     * @param cityCode
     * @param weather
     */
    private void updateWeatherInfoByCityCode(int cityCode, Weather weather) {
        List<WeatherAndCityModel> models = livedata.getValue();
        for (int i = 0; i < models.size(); i++) {
            WeatherAndCityModel weatherAndCityModel = models.get(i);
            int code = weatherAndCityModel.getCity().getCityCode();
            if (code == cityCode) {
                weatherAndCityModel.setWeather(weather);
            }
        }
        livedata.postValue(models);
    }

    /**
     * 清空数据
     */
    public void clearLivedata() {
        livedata.setValue(null);
        livedata = null;
    }

}
