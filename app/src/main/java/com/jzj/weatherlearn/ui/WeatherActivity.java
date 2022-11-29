package com.jzj.weatherlearn.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.model.WeatherAndCityModel;
import com.jzj.weatherlearn.service.AutoUpdateWeatherInfoService;
import com.jzj.weatherlearn.tool.SkyconUtil;
import com.jzj.weatherlearn.ui.adapter.ViewPagerFragmentStateAdapter;
import com.jzj.weatherlearn.ui.widget.ViewPagerIndicator;
import com.jzj.weatherlearn.viewmodel.WeatherAndCityViewModel;
import com.rainy.weahter_bg_plug.WeatherBg;
import com.rainy.weahter_bg_plug.utils.WeatherUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 天气展示容器,数据展示由WeatherFragment负责
 */
public class WeatherActivity extends AppCompatActivity {

    private String TAG;
    public static final int WEATHER_BG_UPDATE_MESSAGE = 0;

    private int mCitySize;
    private WeatherBg weatherBg;
    private List<WeatherFragment> mFragments;
    private ViewPagerFragmentStateAdapter mAdapter;
    private WeatherAndCityViewModel viewModel;

    private FloatingActionButton addCityBtn;
    private ViewPager2 viewPage;
    private ViewPagerIndicator indicator;

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

        @Override
        public void loadWeatherInfo(int cityCode) {
            List<City> cityList = CitySetting.getInstance().getCacheCities();
            for (int i = 0; i < cityList.size(); i++) {
                if (cityList.get(i).getCityCode() == cityCode) {
                    viewModel.getWeatherInfo(cityList.get(i));
                }
            }
        }

        @Override
        public void loadWeatherInfoWithNetwork(int cityCode) {
            List<City> cityList = CitySetting.getInstance().getCacheCities();
            for (int i = 0; i < cityList.size(); i++) {
                if (cityList.get(i).getCityCode() == cityCode) {
                    viewModel.getWeatherInfoFromNetwork(cityList.get(i));
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        /**
         * 参数初始化
         */
        init();
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
        addCityBtn.setSize(FloatingActionButton.SIZE_MINI);
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
        /**
         * 绑定自动更新天气缓存服务 8小时更新一次缓存
         */
        Intent intent = new Intent(WeatherActivity.this, AutoUpdateWeatherInfoService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 城市数量变动,重新加载fragments和指示器
         */
        int size = CitySetting.getInstance().getCachesCitiesSize();
        if (size != mCitySize) {
            if (CitySetting.getInstance().getCachesCitiesSize() == 0) {
                Intent intent = new Intent(WeatherActivity.this, CitySelectActivity.class);
                startActivity(intent);
            } else {
                clearFragments();
                loadCityDataFragments();
                mAdapter = new ViewPagerFragmentStateAdapter(getSupportFragmentManager(), getLifecycle(), mFragments);
                viewPage.setAdapter(mAdapter);
                indicator.setTotalIndex(mFragments.size());
                indicator.setCurrentIndex(0);
                viewPage.setCurrentItem(0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearFragments();
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
                break;
            case R.id.city_refresh:
                WeatherFragment fragment = (WeatherFragment) mFragments.get(viewPage.getCurrentItem());
                fragment.sendMessage(null, WeatherFragment.WEATHER_ON_NETWORD_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化
     */
    private void init() {
        TAG = "WeatherActivity";
        mCitySize = 0;
        mFragments = new ArrayList<>();

        viewModel = new ViewModelProvider(this).get(WeatherAndCityViewModel.class);
        viewModel.getLiveData().observe(this, new Observer<List<WeatherAndCityModel>>() {
            @Override
            public void onChanged(List<WeatherAndCityModel> weatherAndCityModels) {
                if (weatherAndCityModels == null) {
                    return;
                }
                WeatherFragment fragment = mFragments.get(viewPage.getCurrentItem());
                WeatherAndCityModel model = null;
                for (int i = 0; i < weatherAndCityModels.size(); i++) {
                    if (weatherAndCityModels.get(i).getCity().getCityCode() == fragment.getCityCode()) {
                        model = weatherAndCityModels.get(i);
                        break;
                    }
                }
                if (model != null && model.getWeather() != null) {
                    sendMessage(model.getWeather(), WEATHER_BG_UPDATE_MESSAGE);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("weather", model.getWeather());
                    map.put("city", model.getCity());
                    fragment.sendMessage(map, WeatherFragment.WEATHER_ON_RESPONSE);
                }
            }
        });
    }

    /**
     * 加载fragments
     */
    private void loadCityDataFragments() {
        mCitySize = CitySetting.getInstance().getCachesCitiesSize();
        mFragments.clear();
        List<WeatherAndCityModel> models = new ArrayList<>();
        for (int i = 0; i < mCitySize; i++) {
            City city = CitySetting.getInstance().getCacheCities().get(i);
            models.add(new WeatherAndCityModel(city));
            int cityCode = city.getCityCode();
            mFragments.add(WeatherFragment.newInstance(cityCode, weatherBgCallback));
        }
        viewModel.getLiveData().setValue(models);
    }

    /**
     * 清理fragment缓存
     */
    private void clearFragments() {
        //清理fragment缓存
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.commitNow();
            mFragments.clear();
        }
    }

    /**
     * 发送天气信息更新UI的消息
     */
    public void sendMessage(Object object, int what) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = object;
        mHandler.sendMessage(message);
    }

}