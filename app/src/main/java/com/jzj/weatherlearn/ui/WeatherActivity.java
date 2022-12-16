package com.jzj.weatherlearn.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.databinding.ActivityWeatherBinding;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.model.WeatherAndCityModel;
import com.jzj.weatherlearn.ui.base.BaseActivity;
import com.jzj.weatherlearn.util.SkyconUtil;
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
public class WeatherActivity extends BaseActivity<ActivityWeatherBinding>
{
	public static final int WEATHER_BG_UPDATE_MESSAGE = 0;
	public static final int WEATHER_ACTIVITY_FINISH = 1;
	public static final int WEATHER_ACTIVITY_DATA_UPDATE = 2;

	private int mCitySize = 0;
	private String mTitle;
	private WeatherBg weatherBg;
	private List<WeatherFragment> mFragments = new ArrayList<>();
	private ViewPagerFragmentStateAdapter mAdapter;
	private WeatherAndCityViewModel viewModel;

	private Toolbar toolbar;
	private FloatingActionButton addCityBtn;
	private ViewPager2 viewPage;
	private ViewPagerIndicator indicator;
	private AppBarLayout appBarLayout;

	private Handler mHandler = new WeatherActivityHandler();

	private class WeatherActivityHandler extends Handler
	{
		@Override
		public void handleMessage(@NonNull Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				//更新背景动画
				case WEATHER_BG_UPDATE_MESSAGE:
					Weather weather = (Weather) msg.obj;
					String skycon = weather.result.daily.skycon.get(0).value;
					String weatherType = SkyconUtil.handleSkyconToWeatherBgType(skycon);
					if (weatherBg != null) weatherBg.changeWeather(weatherType);
					break;
				case WEATHER_ACTIVITY_DATA_UPDATE:
					WeatherAndCityModel weatherAndCityModel = (WeatherAndCityModel) msg.obj;
					viewDataBinding.setWeatherAndCityModel(weatherAndCityModel);
					break;
				case WEATHER_ACTIVITY_FINISH:
					finish();
					break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//隐藏状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		toolbar = findViewById(R.id.weather_show_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(null);

		/**
		 * viewmodel
		 */
		viewModel = new ViewModelProvider(this).get(WeatherAndCityViewModel.class);
		viewModel.getLiveData().observe(this, new Observer<List<WeatherAndCityModel>>()
		{
			@Override
			public void onChanged(List<WeatherAndCityModel> weatherAndCityModels)
			{
				if (weatherAndCityModels == null || viewPage.getCurrentItem() < 0)
				{
					return;
				}
				WeatherFragment fragment = mFragments.get(viewPage.getCurrentItem());
				WeatherAndCityModel model = null;
				for (int i = 0; i < weatherAndCityModels.size(); i++)
				{
					if (weatherAndCityModels.get(i)
							.getCity()
							.getCityCode() == fragment.getCityCode())
					{
						model = weatherAndCityModels.get(i);
						break;
					}
				}
				if (model != null && model.getWeather() != null)
				{
					mTitle = model.getCity().getCityName();
					sendMessage(model.getWeather(), WEATHER_BG_UPDATE_MESSAGE);
					sendMessage(model, WEATHER_ACTIVITY_DATA_UPDATE);
					HashMap<String, Object> map = new HashMap<>();
					map.put("weather", model.getWeather());
					map.put("city", model.getCity());
					fragment.sendMessage(map, WeatherFragment.WEATHER_ON_RESPONSE);
				}
			}
		});
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
		addCityBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
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
		viewPage.setOffscreenPageLimit(mFragments.size());
		viewPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
		{
			@Override
			public void onPageSelected(int position)
			{
				super.onPageSelected(position);
				viewPage.setCurrentItem(position);
				indicator.setCurrentIndex(position);
				mFragments.get(position).sendActivityUpdateMessage();
			}
		});
		/**
		 * WeatherBg
		 */
		weatherBg = findViewById(R.id.weather_bg);
		weatherBg.changeWeather(WeatherUtil.WeatherType.sunny);
		/**
		 * AppBarLayout
		 */
		appBarLayout = findViewById(R.id.appbar_layout);
		appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener()
		{
			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
			{
				//垂直方向偏移量
				int offset = Math.abs(verticalOffset);
				//最大实际偏移量
				int scrollRange = appBarLayout.getTotalScrollRange();
				if (scrollRange - offset <= 50)
				{
					getSupportActionBar().setTitle(mTitle);
				} else
				{
					getSupportActionBar().setTitle(null);
				}
			}
		});
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		/**
		 * 城市数量变动,重新加载fragments和指示器
		 */
		int size = CitySetting.getInstance().getCachesCitiesSize();
		if (size != mCitySize)
		{
			if (size == 0)
			{
				Toast.makeText(this, "请重新选择城市", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(WeatherActivity.this, CitySelectActivity.class);
				startActivity(intent);
				finish();
			} else
			{
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
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();
		switch (id)
		{
			case R.id.city_manage:
				Intent intent = new Intent(WeatherActivity.this, CityManageActivity.class);
				startActivity(intent);
				break;
			case R.id.city_refresh:
				WeatherFragment fragment = (WeatherFragment) mFragments.get(viewPage.getCurrentItem());
				fragment.sendMessage(null, WeatherFragment.WEATHER_ON_NETWORD_REQUEST);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 加载fragments
	 */
	private void loadCityDataFragments()
	{
		mCitySize = CitySetting.getInstance().getCachesCitiesSize();
		mFragments.clear();
		List<WeatherAndCityModel> models = new ArrayList<>();
		for (int i = 0; i < mCitySize; i++)
		{
			City city = CitySetting.getInstance().getCacheCities().get(i);
			models.add(new WeatherAndCityModel(city));
			int cityCode = city.getCityCode();
			mFragments.add(WeatherFragment.newInstance(cityCode));
		}
		viewModel.getLiveData().setValue(models);
	}

	/**
	 * 清理fragment缓存
	 */
	private void clearFragments()
	{
		//清理fragment缓存
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments.size() > 0)
		{
			for (Fragment fragment : fragments)
			{
				fragmentTransaction.remove(fragment);
			}
			fragmentTransaction.commitNow();
			mFragments.clear();
		}
	}

	/**
	 * 发送天气信息更新UI的消息
	 */
	public void sendMessage(Object object, int what)
	{
		Message message = Message.obtain();
		message.what = what;
		message.obj = object;
		mHandler.sendMessage(message);
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_weather;
	}

}