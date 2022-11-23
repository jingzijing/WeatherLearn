package com.jzj.weatherlearn.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.global.SharedPreferencesManager;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.tool.ApiUtil;
import com.jzj.weatherlearn.tool.DataUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoUpdateWeatherInfoService extends Service {

    private String TAG = AutoUpdateWeatherInfoService.class.getName();
    private SharedPreferences sharedPreferences;
    private final IBinder binder = new UpdateWeatherBinder();

    public class UpdateWeatherBinder extends Binder {
        public AutoUpdateWeatherInfoService getService() {
            return AutoUpdateWeatherInfoService.this;
        }
    }

    public AutoUpdateWeatherInfoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = SharedPreferencesManager.getSharedPreferences(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //8小时
        int timeToMills = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + timeToMills;
        Intent i = new Intent(this, AutoUpdateWeatherInfoService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_IMMUTABLE);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferencesManager.itemDestroy(TAG);
    }

    /**
     * 更新天气缓存信息
     */
    private void updateWeather() {
        List<City> cityList = CitySetting.getInstance().getCacheCities(sharedPreferences);
        for (int i = 0; i < cityList.size(); i++) {
            //url拼接
            String url = ApiUtil.WEATHER_API_URL.replace(ApiUtil.REPLACE_STR, cityList.get(i).getLon() + "," + cityList.get(i).getLat());
            int cityCode = cityList.get(i).getCityCode();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    //发送请求并回调
                    client.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    String responseStr = response.body().string();
                                    //写入缓存
                                    if (responseStr != null) {
                                        DataUtil.writeSharedPreferences(String.valueOf(cityCode), responseStr, sharedPreferences);
                                    }
                                }
                            });
                }
            }).start();
        }
    }
}