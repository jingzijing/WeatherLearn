package com.jzj.weatherlearn.model.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 日出日落时间，当地时区的时刻
 */
public class Astro {

    public String date;

    public Sunrise sunrise;

    public Sunset sunset;

    //日出
    public class Sunrise {
        public String time;
    }

    //日落
    public class Sunset {
        public String time;
    }

}
