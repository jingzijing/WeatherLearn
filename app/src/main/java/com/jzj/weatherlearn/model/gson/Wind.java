package com.jzj.weatherlearn.model.gson;

/**
 * wind 全天地表风速
 * wind_08h_20h 白天地表风速
 * wind_20h_32h 夜晚地表风速
 */
public class Wind {

    public String date;

    public Max max;

    public Min min;

    public Avg avg;

    public class Max {

        public Float speed;

        public Float direction;

    }

    public class Min {

        public Float speed;

        public Float direction;

    }

    public class Avg {
        //10米风速
        public Float speed;

        public Float direction;

    }

}
