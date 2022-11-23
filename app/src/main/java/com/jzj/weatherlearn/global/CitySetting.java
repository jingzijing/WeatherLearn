package com.jzj.weatherlearn.global;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.tool.DataUtil;
import com.jzj.weatherlearn.tool.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供城市列表信息
 */
public class CitySetting {
    public static final String CACHE_CITY = "cache_city";
    private char separator = '-';
    private static CitySetting instance;
    //省
    public static final int CITY_LEVEL_PROVINCE = 0;
    //市
    public static final int CITY_LEVEL_CITY = 1;
    //区县
    public static final int CITY_LEVEL_DISTRICT = 2;
    /**
     * 被选择的城市集
     */
    private List<City> cacheCityList;

    private CitySetting() {
        cacheCityList = new ArrayList<>();
    }

    public static synchronized CitySetting getInstance() {
        if (instance == null) {
            instance = new CitySetting();
        }
        return instance;
    }

    public void addCity(City city, SharedPreferences sharedPreferences) {
        cacheCityList.add(0, city);
        String cacheCityListJson = GsonUtil.formatListToJson(cacheCityList);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                sharedPreferences.edit().putString(CACHE_CITY, cacheCityListJson).apply();
            }
        });
    }

    public void deleteCity(City city, SharedPreferences sharedPreferences) {
        cacheCityList.remove(city);
        String cacheCityListJson = GsonUtil.formatListToJson(cacheCityList);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                sharedPreferences.edit().putString(CACHE_CITY, cacheCityListJson).apply();
            }
        });
    }

    /**
     * 缓存城市列表
     */
    public List<City> getCacheCities(SharedPreferences sharedPreferences) {

        if (cacheCityList.size() == 0) {
            List list = GsonUtil.formatJsonToList(DataUtil.readSharedPreferences(CACHE_CITY, sharedPreferences));
            if (list != null) {
                cacheCityList.addAll(list);
            }
        }

        return cacheCityList;
    }

}
