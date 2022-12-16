package com.jzj.weatherlearn.util;

import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.model.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 资料库SQLite,SharedPreferences读写方法集
 */
public class DataUtil {
    /**
     * 往SharedPreferences写入资料
     *
     * @param key
     * @param content
     */
    public static void writeSharedPreferences(String key, String content, SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, content);
        editor.apply();
    }

    /**
     * 读SharedPreferences资料
     *
     * @param key
     * @return
     */
    public static String readSharedPreferences(String key, SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return null;
        }
        String content = sharedPreferences.getString(key, null);
        return content;
    }

    /**
     * 从文件city_data中读取城市列表
     */
    public static List<City> loadCityInfo() throws IOException {
        List<City> cityList = new ArrayList<>();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        AssetManager assetManager = App.context.getResources().getAssets();
        try {
            is = assetManager.open("city_data");
            isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                //每一行进行逗号分割后的字符,按照顺序映射至对象
                if (split.length == 4) {
                    City city = new City();
                    city.setCityCode(Integer.parseInt(split[0]));
                    city.setCityName(split[1]);
                    city.setLon(Double.parseDouble(split[2]));
                    city.setLat(Double.parseDouble(split[3]));
                    cityList.add(city);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return cityList;
    }

}
