package com.jzj.weatherlearn.tool;

public class ApiUtil {

    //彩云天气token
    public static final String TOKEN = "填写你申请的token";
    //URL替换值部分
    public static final String REPLACE_STR = "#longitude,latitude#";
    //彩云天气天级别预报API
    public static final String WEATHER_API_URL = "https://api.caiyunapp.com/v2.6/" + TOKEN + "/" + REPLACE_STR + "/daily?dailysteps=10";

}
