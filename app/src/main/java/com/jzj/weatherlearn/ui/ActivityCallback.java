package com.jzj.weatherlearn.ui;

import com.jzj.weatherlearn.model.Weather;

/**
 * activity提供给fragment的回调
 */
public interface ActivityCallback {

    /**
     * 改变背景图片
     * @param weather
     * @param messageWhat
     */
    void handleWeatherBg(Weather weather, int messageWhat);

}
