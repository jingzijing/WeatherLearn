package com.jzj.weatherlearn.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.CityInfo;
import com.jzj.weatherlearn.model.Weather;

import java.util.List;

/**
 * gson转换格式方法包装集
 */
public class GsonUtil {

    /**
     * List集合转换成Json字串
     * List成员需要实现序列化接口
     *
     * @param list
     * @return
     */
    public static String formatListToJson(List list) {

        if (list.size() == 0) {
            return null;
        }

        Gson gson = new Gson();
        String jsonStr = gson.toJson(list);
        gson = null;
        return jsonStr;
    }

    /**
     * json转换成list集合
     * @param json
     * @return
     */
    public static List formatJsonToList(String json) {
        List list = null;
        if (json != null && (!"".equals(json))) {
            Gson gson = new Gson();
            list = gson.fromJson(json, new TypeToken<List<City>>(){}.getType());
            gson = null;
        }
        return list;
    }

    /**
     * 天气预报数据处理
     * @param json
     * @return
     */
    public static Weather handlerWeatherJson(String json) {
        Weather weather = null;
        if (json != null && (!"".equals(json))) {
            Gson gson = new Gson();
            weather = gson.fromJson(json, Weather.class);
            gson = null;
        }
        return weather;
    }

    /**
     * 城市信息数据处理
     * @param json
     * @return
     */
    public static CityInfo handlerCityInfoJson(String json) {
        CityInfo cityInfo = null;
        if (json != null && (!"".equals(json))) {
            Gson gson = new Gson();
            cityInfo = gson.fromJson(json, CityInfo.class);
            gson = null;
        }
        return cityInfo;
    }

}
