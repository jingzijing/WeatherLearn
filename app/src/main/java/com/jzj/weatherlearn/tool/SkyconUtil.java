package com.jzj.weatherlearn.tool;

import com.rainy.weahter_bg_plug.utils.WeatherUtil;

/**
 * 处理彩云api的天气信息
 */
public class SkyconUtil {
    /**
     * 晴（白天）cloudrate < 0.2
     * 晴（夜间）cloudrate < 0.2
     */
    public static final String CLEAR_DAY = "CLEAR_DAY";
    public static final String CLEAR_NIGHT = "CLEAR_NIGHT";
    /**
     * 多云（白天）0.8 >= cloudrate > 0.2
     * 多云（夜间）0.8 >= cloudrate > 0.2
     */
    public static final String PARTLY_CLOUDY_DAY = "PARTLY_CLOUDY_DAY";
    public static final String PARTLY_CLOUDY_NIGHT = "PARTLY_CLOUDY_NIGHT";
    /**
     * 阴 cloudrate > 0.8
     */
    public static final String CLOUDY = "CLOUDY";
    /**
     * 轻度雾霾PM2.5 100~150
     * 中度雾霾PM2.5 150~200
     * 重度雾霾PM2.5 > 200
     */
    public static final String LIGHT_HAZE = "LIGHT_HAZE";
    public static final String MODERATE_HAZE = "MODERATE_HAZE";
    public static final String HEAVY_HAZE = "HEAVY_HAZE";
    /**
     * 小雨
     * 中雨
     * 大雨
     * 暴雨
     */
    public static final String LIGHT_RAIN = "LIGHT_RAIN";
    public static final String MODERATE_RAIN = "MODERATE_RAIN";
    public static final String HEAVY_RAIN = "HEAVY_RAIN";
    public static final String STORM_RAIN = "STORM_RAIN";
    /**
     * 雾 	能见度低，湿度高，风速低，温度低
     */
    public static final String FOG = "FOG";
    /**
     * 小雪
     * 中雪
     * 大雪
     * 暴雪
     */
    public static final String LIGHT_SNOW = "LIGHT_SNOW";
    public static final String MODERATE_SNOW = "MODERATE_SNOW";
    public static final String HEAVY_SNOW = "HEAVY_SNOW";
    public static final String STORM_SNOW = "STORM_SNOW";
    /**
     * 浮尘 AQI > 150, PM10 > 150，湿度 < 30%，风速 < 6 m/s
     * 沙尘 AQI > 150, PM10> 150，湿度 < 30%，风速 > 6 m/s
     */
    public static final String DUST = "DUST";
    public static final String SAND = "SAND";
    /**
     * 大风
     */
    public static final String WIND = "WIND";

    //weatherBg type 和 skycon映射
    public static String handleSkyconToWeatherBgType(String skycon) {
        //默认大雨
        String weatherType = WeatherUtil.WeatherType.heavyRainy;
        switch (skycon) {
            case CLEAR_DAY:
                weatherType = WeatherUtil.WeatherType.sunny;
                break;
            case CLEAR_NIGHT:
                weatherType = WeatherUtil.WeatherType.sunnyNight;
                break;
            case WIND:
            case PARTLY_CLOUDY_DAY:
            case CLOUDY:
                weatherType = WeatherUtil.WeatherType.cloudy;
                break;
            case PARTLY_CLOUDY_NIGHT:
                weatherType = WeatherUtil.WeatherType.cloudyNight;
                break;
            case LIGHT_HAZE:
            case MODERATE_HAZE:
            case HEAVY_HAZE:
                weatherType = WeatherUtil.WeatherType.hazy;
                break;
            case LIGHT_RAIN:
                weatherType = WeatherUtil.WeatherType.lightRainy;
                break;
            case MODERATE_RAIN:
                weatherType = WeatherUtil.WeatherType.middleRainy;
                break;
            case HEAVY_RAIN:
            case STORM_RAIN:
                weatherType = WeatherUtil.WeatherType.heavyRainy;
                break;
            case FOG:
                weatherType = WeatherUtil.WeatherType.foggy;
                break;
            case LIGHT_SNOW:
                weatherType = WeatherUtil.WeatherType.lightSnow;
                break;
            case MODERATE_SNOW:
                weatherType = WeatherUtil.WeatherType.middleSnow;
                break;
            case HEAVY_SNOW:
            case STORM_SNOW:
                weatherType = WeatherUtil.WeatherType.heavySnow;
                break;
            case DUST:
            case SAND:
                weatherType = WeatherUtil.WeatherType.dusty;
                break;
            default:
                break;
        }
        return weatherType;
    }
}
