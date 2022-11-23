package com.jzj.weatherlearn.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.jzj.weatherlearn.model.City;

import java.util.List;

/**
 * 城市分级查询条件根据城市代号硬编码规律书写,具体规律可从city_data文件寻找
 */
@Dao
public interface CityDao {
    /**
     * 查询所有城市
     *
     * @return
     */
    @Query("SELECT * FROM City")
    List<City> getCityList();

    @Query("SELECT * FROM City")
    LiveData<List<City>> getCityLiveData();
    /**
     * 查询省级城市
     *
     * @return
     */
    @Query("SELECT * FROM City WHERE substr(city_code,3,6) = '0000' ")
    List<City> getCityListLevelProvince();

    @Query("SELECT * FROM City WHERE substr(city_code,3,6) = '0000' ")
    LiveData<List<City>> getCityLiveDataLevelProvince();

    /**
     * 查询市级城市
     *
     * @return
     */
    @Query("SELECT * FROM City WHERE substr(city_code,5,6) = '00' AND substr(city_code,3,4) != '00' ")
    List<City> getCityListLevelCity();

    @Query("SELECT * FROM City WHERE substr(city_code,5,6) = '00' AND substr(city_code,3,4) != '00' ")
    LiveData<List<City>> getCityLiveDataLevelCity();

    /**
     * 查询区县级城市
     *
     * @return
     */
    @Query("SELECT * FROM City WHERE substr(city_code,5,6) != '00' AND substr(city_code,3,4) != '00' ")
    List<City> getCityListLevelDistrict();

    @Query("SELECT * FROM City WHERE substr(city_code,5,6) != '00' AND substr(city_code,3,4) != '00' ")
    LiveData<List<City>> getCityLiveDataLevelDistrict();

    /**
     * 带模糊查询所有城市
     *
     * @return
     */
    @Query("SELECT * FROM City WHERE city_name LIKE '%' || :searchCity || '%'")
    List<City> getCityListWithSearch(String searchCity);

    @Query("SELECT * FROM City WHERE city_name LIKE '%' || :searchCity || '%'")
    LiveData<List<City>> getCityLiveDataWithSearch(String searchCity);

    /**
     * 带模糊条件查询省级城市
     *
     * @return
     */
    @Query("SELECT * FROM City WHERE substr(city_code,3,6) = '0000' AND city_name LIKE '%' || :searchCity || '%' ")
    List<City> getCityListLevelProvinceWithSearch(String searchCity);

    @Query("SELECT * FROM City WHERE substr(city_code,3,6) = '0000' AND city_name LIKE '%' || :searchCity || '%' ")
    LiveData<List<City>> getCityLiveDataLevelProvinceWithSearch(String searchCity);

    /**
     * 带模糊条件查询市级城市
     *
     * @return
     */
    @Query("SELECT * FROM City WHERE substr(city_code,5,6) = '00' AND substr(city_code,3,4) != '00' AND city_name LIKE '%' || :searchCity || '%' ")
    List<City> getCityListLevelCityWithSearch(String searchCity);

    @Query("SELECT * FROM City WHERE substr(city_code,5,6) = '00' AND substr(city_code,3,4) != '00' AND city_name LIKE '%' || :searchCity || '%' ")
    LiveData<List<City>> getCityLiveDataLevelCityWithSearch(String searchCity);

    /**
     * 带模糊条件查询区县级城市
     *
     * @return
     */
    @Query("SELECT * FROM City WHERE substr(city_code,5,6) != '00' AND substr(city_code,3,4) != '00' AND city_name LIKE '%' || :searchCity || '%' ")
    List<City> getCityListLevelDistrictWithSearch(String searchCity);

    @Query("SELECT * FROM City WHERE substr(city_code,5,6) != '00' AND substr(city_code,3,4) != '00' AND city_name LIKE '%' || :searchCity || '%' ")
    LiveData<List<City>> getCityLiveDataLevelDistrictWithSearch(String searchCity);

    /**
     * 插入一个城市
     *
     * @param city
     */
    @Insert
    void insertCity(City city);

    /**
     * 删除一个城市
     *
     * @param city
     */
    @Delete
    void deleteCity(City city);

}
