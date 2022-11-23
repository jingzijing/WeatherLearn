package com.jzj.weatherlearn.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jzj.weatherlearn.db.CityDao;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;

import java.util.List;

/**
 * 为界面提供城市数据
 */
public class CityViewModel extends ViewModel {
    //与cityRepository的成员cityLiveData为同一依赖,值由cityRepository负责改变
    MutableLiveData<List<City>> cityLiveData;
    CityDao cityDao;

    public CityViewModel() {
        cityDao = App.database.cityDao();
        cityLiveData = new MutableLiveData<>();
    }

    /**
     * 获取所有城市
     */
    public void getCityList() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                cityLiveData.postValue(cityDao.getCityList());
            }
        });
    }

    /**
     * 获取分级城市
     *
     * @param level
     */
    public void getCityLiveDataWithLevel(int level) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                switch (level) {
                    case CitySetting.CITY_LEVEL_PROVINCE:
                        cityLiveData.postValue(cityDao.getCityListLevelProvince());
                        break;
                    case CitySetting.CITY_LEVEL_CITY:
                        cityLiveData.postValue(cityDao.getCityListLevelCity());
                        break;
                    case CitySetting.CITY_LEVEL_DISTRICT:
                        cityLiveData.postValue(cityDao.getCityListLevelDistrict());
                        break;
                    default:
                        cityLiveData.postValue(cityDao.getCityList());
                        break;
                }
            }
        });
    }

    /**
     * 带模糊获取分级城市
     *
     * @param level
     */
    public void getCityLiveDataWithLevel(int level, String searchCity) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                switch (level) {
                    case CitySetting.CITY_LEVEL_PROVINCE:
                        cityLiveData.postValue(cityDao.getCityListLevelProvinceWithSearch(searchCity));
                        break;
                    case CitySetting.CITY_LEVEL_CITY:
                        cityLiveData.postValue(cityDao.getCityListLevelCityWithSearch(searchCity));
                        break;
                    case CitySetting.CITY_LEVEL_DISTRICT:
                        cityLiveData.postValue(cityDao.getCityListLevelDistrictWithSearch(searchCity));
                        break;
                    default:
                        cityLiveData.postValue(cityDao.getCityListWithSearch(searchCity));
                        break;
                }
            }
        });
    }

    /**
     * @return
     */
    public MutableLiveData<List<City>> getCityLiveData() {
        return cityLiveData;
    }

}
