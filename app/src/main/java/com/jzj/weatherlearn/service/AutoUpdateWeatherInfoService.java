package com.jzj.weatherlearn.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.util.DataUtil;
import com.jzj.weatherlearn.util.NetworkUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 应用处于后台且未清理内存,2小时更新一次天气缓存
 * 启动该服务,更新一次天气缓存
 */
public class AutoUpdateWeatherInfoService extends Service {
    //广播
    private static final String ALARM_ACTION = "UPDATE_WEATHER_DATA_ACTION";
    //2小时
    private static final int timeToMills = 2 * 60 * 60 * 1000;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新天气缓存
            updateWeather();
            setTimingType(false);
        }
    };

    public AutoUpdateWeatherInfoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        updateWeather();
        IntentFilter intentFilter = new IntentFilter(ALARM_ACTION);
        registerReceiver(alarmReceiver, intentFilter);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ALARM_ACTION), PendingIntent.FLAG_IMMUTABLE);
        setTimingType(true);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alarmReceiver);
        alarmManager.cancel(pendingIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 更新天气缓存信息
     */
    private void updateWeather() {
        List<City> cityList = CitySetting.getInstance().getCacheCities();
        for (int i = 0; i < cityList.size(); i++) {
            City city = cityList.get(i);
            NetworkUtil.sendWeatherRequest(city, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseStr = response.body().string();
                    //写入缓存
                    if (responseStr != null) {
                        DataUtil.writeSharedPreferences(String.valueOf(city.getCityCode()), responseStr, App.sharedPreferences);
                    }
                }
            });
        }
    }

    /**
     * 根据版本设置定时类型
     *
     * @param firstSet 第一次设置闹钟否
     */
    private void setTimingType(boolean firstSet) {
        if (alarmManager == null || pendingIntent == null)
            return;

        int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long triggerAtTime = SystemClock.elapsedRealtime() + timeToMills;
        //高于4.4版本,任务执行后需要再次设置闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, triggerAtTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(type, triggerAtTime, pendingIntent);
        } else {
            if (firstSet) {
                alarmManager.setRepeating(type, SystemClock.elapsedRealtime(), timeToMills, pendingIntent);
            }
        }
    }
}