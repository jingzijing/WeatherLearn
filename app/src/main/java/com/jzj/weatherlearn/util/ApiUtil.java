package com.jzj.weatherlearn.util;

public class ApiUtil {

    //彩云天气token
    public static final String TOKEN = "5jDadkR320jXIEND";
    //彩云URL替换值部分
    public static final String REPLACE_STR = "#longitude,latitude#";
    //彩云天气天级别预报API
    public static final String WEATHER_API_URL = "https://api.caiyunapp.com/v2.6/" + TOKEN + "/" + REPLACE_STR + "/daily?dailysteps=10";
    //百度地图ak码
    public static final String BAIDU_MAP_AK = "GBUMDq2YrHT1LBblCfupO1GPYysUvhGf";
    //百度地图获取城市信息API
    public static final String BAIDU_MAP_API_URL = "https://api.map.baidu.com/geocoder?output=json&location=" + REPLACE_STR + "&ak=" + TOKEN;
}
