package com.jzj.weatherlearn.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.service.AutoUpdateWeatherInfoService;
import com.jzj.weatherlearn.tool.DataUtil;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();
    private DataInitFinishCallback callback = new DataInitFinishCallback() {
        @Override
        public void onFinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * 启动后台服务 2小时更新一次缓存
                     */
                    Intent intent = new Intent(MainActivity.this, AutoUpdateWeatherInfoService.class);
                    startService(intent);

                    jump();
                    finish();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataInit();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 跳转动作
     */
    private void jump() {
        /**
         * 没有缓存城市,先选择城市
         */
        Intent intent;
        List<City> cacheCityList = CitySetting.getInstance().getCacheCities();
        if (cacheCityList.size() == 0) {
            intent = new Intent(MainActivity.this, CitySelectActivity.class);
            Toast.makeText(this, "<没有缓存的城市,请选择城市>", Toast.LENGTH_SHORT).show();
        } else {
            intent = new Intent(MainActivity.this, WeatherActivity.class);
        }
        startActivity(intent);
    }

    /**
     * 预加载数据库信息
     */
    private void dataInit() {
        List<City> listLiveData = App.database.cityDao().getCityList();
        try {
            if (listLiveData == null || listLiveData.size() == 0) {
                List<City> cityList = DataUtil.loadCityInfo();
                for (City city : cityList) {
                    App.database.cityDao().insertCity(city);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        callback.onFinish();
    }

    interface DataInitFinishCallback {
        void onFinish();
    }

}