package com.jzj.weatherlearn.tool;

import android.graphics.drawable.Drawable;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.model.Weather;
import com.rainy.weahter_bg_plug.utils.WeatherUtil;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 天气现象图标映射
     */
    public static Drawable handlerWeatherIcno(String skyconVal) {
        Drawable drawable = null;
        switch (skyconVal) {
            //晴天
            case SkyconUtil.CLEAR_DAY:
            case SkyconUtil.CLEAR_NIGHT:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_clear_day, null);
                break;
            //多云
            case SkyconUtil.PARTLY_CLOUDY_DAY:
            case SkyconUtil.PARTLY_CLOUDY_NIGHT:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_partly_cloudy_day, null);
                break;
            //阴天
            case SkyconUtil.CLOUDY:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_cloudy_day, null);
                break;
            //雨天
            case SkyconUtil.LIGHT_RAIN:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_light_rain, null);
                break;
            case SkyconUtil.MODERATE_RAIN:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_moderate_rain, null);
                break;
            case SkyconUtil.HEAVY_RAIN:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_heavy_rain, null);
                break;
            case SkyconUtil.STORM_RAIN:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_storm_rain, null);
                break;
            //雾
            case SkyconUtil.FOG:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_fog, null);
                break;
            //雪
            case SkyconUtil.LIGHT_SNOW:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_light_snow, null);
                break;
            case SkyconUtil.MODERATE_SNOW:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_moderate_snow, null);
                break;
            case SkyconUtil.HEAVY_SNOW:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_heavy_snow, null);
                break;
            case SkyconUtil.STORM_SNOW:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_storm_snow, null);
                break;
            //尘
            case SkyconUtil.DUST:
            case SkyconUtil.SAND:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_dust_with_wind, null);
                break;
            //风
            case SkyconUtil.WIND:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_wind, null);
                break;
            default:
                drawable = App.context.getResources().getDrawable(R.drawable.ic_partly_cloudy_day, null);
                break;
        }
        return drawable;
    }

    /**
     * 天气现象名字
     */
    public static String getSkyConName(String skyconVal) {
        switch (skyconVal) {
            //晴天
            case SkyconUtil.CLEAR_DAY:
            case SkyconUtil.CLEAR_NIGHT:
                return "晴";
            //多云
            case SkyconUtil.PARTLY_CLOUDY_DAY:
            case SkyconUtil.PARTLY_CLOUDY_NIGHT:
                return "多云";
            //阴天
            case SkyconUtil.CLOUDY:
                return "阴";
            //雨天
            case SkyconUtil.LIGHT_RAIN:
                return "小雨";
            case SkyconUtil.MODERATE_RAIN:
                return "中雨";
            case SkyconUtil.HEAVY_RAIN:
                return "大雨";
            case SkyconUtil.STORM_RAIN:
                return "暴雨";
            //雾
            case SkyconUtil.FOG:
                return "雾";
            //雪
            case SkyconUtil.LIGHT_SNOW:
                return "小雪";
            case SkyconUtil.MODERATE_SNOW:
                return "中雪";
            case SkyconUtil.HEAVY_SNOW:
                return "大雪";
            case SkyconUtil.STORM_SNOW:
                return "暴雪";
            //尘
            case SkyconUtil.DUST:
            case SkyconUtil.SAND:
                return "尘";
            //风
            case SkyconUtil.WIND:
                return "风";
            default:
                return "多云";
        }
    }

    /**
     * 获取生活建议
     *
     * @param weather
     * @return
     */
    public static String getWeatherAdvice(Weather weather) {
        String ln = "\r\n";
        StringBuilder advice = new StringBuilder();
        List<String> adviceList = new ArrayList<>();
        //温度提醒
        if ((weather.result.daily.temperature.get(0).avg < 20)) {
            adviceList.add("天冷请注意添衣,今日平均温度" + weather.result.daily.temperature.get(0).avg + ln);
        }
        //下雨提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.LIGHT_RAIN) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.MODERATE_RAIN)) {
            adviceList.add("今日有雨,出门记得备伞" + ln);
        }
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.HEAVY_RAIN) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.STORM_RAIN)) {
            adviceList.add("今日雨较大,尽量避免出行" + ln);
        }
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.HEAVY_HAZE)) {
            adviceList.add("今日雾霾较重,出门请记得备口罩" + ln);
        }
        //下雪提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.HEAVY_SNOW) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.STORM_SNOW)) {
            adviceList.add("今日雪厚,尽量避免出门" + ln);
        }
        //雾提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.FOG)) {
            adviceList.add("今日有雾,出行请注意安全" + ln);
        }
        //大风提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.WIND)) {
            adviceList.add("今日风较大,出行请注意安全" + ln);
        }
        //阴天提醒
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.CLOUDY) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.PARTLY_CLOUDY_NIGHT) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.PARTLY_CLOUDY_DAY)) {
            adviceList.add("今日多云, 出行请做好应对雨天的准备, 适合出门, 祝今天好心情" + ln);
        }
        //晴天建议
        if (weather.result.daily.skycon.get(0).value.equals(SkyconUtil.CLEAR_DAY) || weather.result.daily.skycon.get(0).value.equals(SkyconUtil.CLEAR_NIGHT)) {
            adviceList.add("今日晴, 出行注意防晒, 适合出门, 祝今天好心情" + ln);
        }
        for (int i = 0; i < adviceList.size(); i++) {
            advice.append((i + 1) + ".  " + adviceList.get(i));
        }
        return advice.toString();
    }
}
