package com.jzj.weatherlearn.global;

import android.app.Application;
import android.content.Context;

import com.jzj.weatherlearn.db.AppDatabase;

public class App extends Application {

    //application context
    public static Context context;
    //数据库实例
    public static AppDatabase database;
    //sharedpreferences名字
    public static final String SP_CACHE = "cache_info";

    @Override
    public void onCreate() {
        super.onCreate();
        context = super.getApplicationContext();
        database = AppDatabase.getInstance();
    }

}
