package com.jzj.weatherlearn.model.gson_weather;

import java.util.List;

public class LifeIndex {

    /**
     * 紫外线指数
     */
    public List<Index> ultraviolet;

    /**
     * 洗车指数
     */
    public List<Index> carWashing;

    /**
     * 	穿衣指数
     */
    public List<Index> dressing;

    /**
     *舒适度指数
     */
    public List<Index> comfort;

    /**
     * 感冒指数
     */
    public List<Index> coldRisk;


    public class Index {

        public String date;

        public String index;

        public String desc;
    }

}
