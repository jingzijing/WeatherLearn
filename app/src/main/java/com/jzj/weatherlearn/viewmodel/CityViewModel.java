package com.jzj.weatherlearn.viewmodel;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.jzj.weatherlearn.db.CityDao;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.CityInfo;
import com.jzj.weatherlearn.tool.ApiUtil;
import com.jzj.weatherlearn.tool.GsonUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 为界面提供城市数据
 */
public class CityViewModel extends AndroidViewModel {
    //与cityRepository的成员cityLiveData为同一依赖,值由cityRepository负责改变
    MutableLiveData<List<City>> cityLiveData;
    CityDao cityDao;
    Context context;

    public CityViewModel(@NonNull Application application) {
        super(application);
        cityDao = App.database.cityDao();
        cityLiveData = new MutableLiveData<>();
        context = application.getApplicationContext();
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

    /**
     * 根据定位获取城市对象
     */
    public void getCityByLatAndLon(Location location, OnNetworkResponseListener callback) {
        if (location == null) {
            callback.onFailure();
            return;
        }
        String url = ApiUtil.BAIDU_MAP_API_URL.replace(ApiUtil.REPLACE_STR, location.getLatitude() + "," + location.getLongitude());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        callback.onFailure();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        CityInfo cityInfo = GsonUtil.handlerCityInfoJson(response.body().string());
                        if (cityInfo == null) {
                            callback.onFailure();
                            return;
                        }
                        if ("OK".equals(cityInfo.status)) {
                            City city = new City(cityInfo.result.cityCode, cityInfo.result.addressComponent.city, cityInfo.result.location.lng, cityInfo.result.location.lat);
                            callback.onResponse(city);
                        } else {
                            callback.onFailure();
                        }
                    }
                });
            }
        });
    }

    public interface OnNetworkResponseListener {
        void onResponse(City city);

        void onFailure();
    }

}
