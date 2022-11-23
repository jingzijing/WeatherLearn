package com.jzj.weatherlearn.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.global.SharedPreferencesManager;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.service.AutoUpdateWeatherInfoService;
import com.jzj.weatherlearn.tool.SkyconUtil;
import com.jzj.weatherlearn.ui.adapter.ViewPagerFragmentStateAdapter;
import com.jzj.weatherlearn.ui.widget.ViewPagerIndicator;
import com.rainy.weahter_bg_plug.WeatherBg;
import com.rainy.weahter_bg_plug.utils.WeatherUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 天气展示容器,数据展示由WeatherFragment负责
 */
public class WeatherActivity extends AppCompatActivity {

    private String TAG = WeatherActivity.class.getName();
    public static final String CITY_SELECT_RESULT_KEY = "resultCity";
    public static final int WEATHER_BG_UPDATE_MESSAGE = 3;
    private int mCitySize = 0;
    private SharedPreferences sharedPreferences;

    private ImageButton addCityBtn;
    private ViewPager2 viewPage;
    private ViewPagerIndicator indicator;
    private WeatherBg weatherBg;
    private List mFragments = new ArrayList<>();
    // onCreate回调时初始化
    private ViewPagerFragmentStateAdapter mAdapter;

    private Handler mHandler = new WeatherActivityHandler();

    private class WeatherActivityHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新背景动画
                case WEATHER_BG_UPDATE_MESSAGE:
                    Weather weather = (Weather) msg.obj;
                    String skycon = weather.result.daily.skycon.get(0).value;
                    String weatherType = SkyconUtil.handleSkyconToWeatherBgType(skycon);
                    if (weatherBg != null) {
                        weatherBg.changeWeather(weatherType);
                    }
                    break;
            }
        }
    }

    ;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AutoUpdateWeatherInfoService autoUpdateWeatherInfoService = ((AutoUpdateWeatherInfoService.UpdateWeatherBinder) service).getService();
            Intent intent = new Intent();
            //绑定成功后启动服务
            autoUpdateWeatherInfoService.onStartCommand(intent, 0, 0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private ActivityCallback weatherBgCallback = new ActivityCallback() {
        @Override
        public void handleWeatherBg(Weather weather, int messageWhat) {
            Message message = Message.obtain();
            message.what = messageWhat;
            message.obj = weather;
            mHandler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        /**
         * sharedPreferences
         */
        sharedPreferences = SharedPreferencesManager.getSharedPreferences(TAG);
        /**
         * 加载fragments
         */
        loadCityDataFragments();
        /**
         * 指示器初始化
         */
        indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        indicator.setTotalIndex(mFragments.size());
        indicator.setCurrentIndex(0);
        /**
         * 浮动按钮 添加城市用
         */
        addCityBtn = findViewById(R.id.add_city_btn);
        addCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WeatherActivity.this, CitySelectActivity.class);
                startActivity(intent);
            }
        });
        /**
         * viewpager
         */
        viewPage = findViewById(R.id.view_page);
        mAdapter = new ViewPagerFragmentStateAdapter(getSupportFragmentManager(), getLifecycle(), mFragments);
        viewPage.setAdapter(mAdapter);
        viewPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewPage.setCurrentItem(position);
                indicator.setCurrentIndex(position);
            }
        });
        /**
         * WeatherBg
         */
        weatherBg = findViewById(R.id.weather_bg);
        weatherBg.changeWeather(WeatherUtil.WeatherType.sunny);
        //绑定自动更新天气缓存服务 8小时更新一次缓存
        Intent intent = new Intent(WeatherActivity.this, AutoUpdateWeatherInfoService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 城市数量变动,重新加载fragments和指示器
         */
        if (CitySetting.getInstance().getCacheCities(sharedPreferences).size() != mCitySize) {
            if (CitySetting.getInstance().getCacheCities(sharedPreferences).size() == 0) {
                Intent intent = new Intent(WeatherActivity.this, CitySelectActivity.class);
                startActivity(intent);
            } else {
                loadCityDataFragments();
                mAdapter.notifyDataSetChanged();
                indicator.setTotalIndex(mFragments.size());
                indicator.setCurrentIndex(0);
                viewPage.setCurrentItem(0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFragments.size() > 0) {
            for (Object fragment : mFragments) {
                ((Fragment) fragment).onDestroy();
            }
        }
        SharedPreferencesManager.itemDestroy(TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.city_manage:
                Intent intent = new Intent(WeatherActivity.this, CityManageActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载fragments
     */
    private void loadCityDataFragments() {
        mCitySize = CitySetting.getInstance().getCacheCities(sharedPreferences).size();
        mFragments.clear();
        for (int i = 0; i < mCitySize; i++) {
            mFragments.add(WeatherFragment.newInstance(i, weatherBgCallback));
        }
    }

}