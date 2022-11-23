package com.jzj.weatherlearn.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * 城市数据结构
 */
@Entity(tableName = "City")

public class City implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    private int id;

    @ColumnInfo(name = "city_code", typeAffinity = ColumnInfo.INTEGER)
    private int cityCode;

    @ColumnInfo(name = "city_name", typeAffinity = ColumnInfo.TEXT)
    private String cityName;

    //经度
    @ColumnInfo(name = "lon", typeAffinity = ColumnInfo.REAL)
    private double lon;

    //纬度
    @ColumnInfo(name = "lat", typeAffinity = ColumnInfo.REAL)
    private double lat;

    public City(int cityCode, String cityName, double lon, double lat) {
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.lon = lon;
        this.lat = lat;
    }

    @Ignore
    public City() {
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
