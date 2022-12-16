package com.jzj.weatherlearn.ui;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.databinding.FragmentWeatherBinding;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.WeatherAndCityModel;
import com.jzj.weatherlearn.model.WeatherListModel;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.ui.adapter.WeatherReportItemListAdapter;
import com.jzj.weatherlearn.ui.base.BaseLazyFragment;
import com.jzj.weatherlearn.viewmodel.WeatherAndCityViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 天气数据展示
 */
public class WeatherFragment extends BaseLazyFragment<FragmentWeatherBinding, WeatherAndCityViewModel>
{
	//更新UI数据的消息标识
	public static final int WEATHER_ON_RESPONSE = 0;
	public static final int WEATHER_ON_NETWORD_REQUEST = 1;

	private int mCityCode;
	private List<WeatherListModel> mWeatherList = new ArrayList<>();
	private WeatherReportItemListAdapter mWeatherReportItemListAdapter;
	private boolean refreshFlag;
	private Handler mHandler = new WeatherFragmentHandler();

	class WeatherFragmentHandler extends Handler
	{
		@Override
		public void handleMessage(@NonNull Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				/**
				 * 更新画面数据
				 */
				case WEATHER_ON_RESPONSE:
					HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
					if (map != null)
					{
						Weather weather = (Weather) map.get("weather");
						City city = (City) map.get("city");
						updateToView(weather, city);
						if (refreshFlag)
						{
							Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
							refreshFlag = false;
						}
					}
					break;
				/**
				 * 刷新天气信息
				 */
				case WEATHER_ON_NETWORD_REQUEST:
					if (!refreshFlag)
					{
						refreshFlag = true;
						viewModel.getWeatherInfoFromNetwork(viewModel.findCityByCode(mCityCode));
					}
					break;
			}
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		/**
		 * recyclerview
		 */
		mWeatherReportItemListAdapter = new WeatherReportItemListAdapter(getActivity(), mWeatherList);
		viewDataBinding.setAdapter(mWeatherReportItemListAdapter);
		viewDataBinding.setLayoutManager(new LinearLayoutManager(getActivity()));
	}

	/**
	 * 提供给外部创建fragment
	 */
	public static WeatherFragment newInstance(int cityCode)
	{
		WeatherFragment weatherFragment = new WeatherFragment();
		weatherFragment.mCityCode = cityCode;
		return weatherFragment;
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

	/**
	 * 更新数据到控件中
	 */
	private void updateToView(Weather weather, City city)
	{
		viewDataBinding.setWeather(weather);
		viewDataBinding.setCity(city);
		mWeatherList = weather.getResultDailyWeatherModel();
		mWeatherReportItemListAdapter.setDatas(mWeatherList);
	}

	@Override
	protected WeatherAndCityViewModel getViewModel()
	{
		return new ViewModelProvider(getActivity()).get(WeatherAndCityViewModel.class);
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.fragment_weather;
	}

	@Override
	protected void loadData()
	{
		viewModel.getWeatherInfo(viewModel.findCityByCode(mCityCode));
	}

	public int getCityCode()
	{
		return mCityCode;
	}

	public void sendActivityUpdateMessage() {
		Weather weather = viewDataBinding.getWeather();
		City city = viewDataBinding.getCity();
		if (weather == null || city == null)
			return;
		WeatherAndCityModel weatherAndCityModel = new WeatherAndCityModel(weather, city);
		((WeatherActivity)getActivity()).sendMessage(weatherAndCityModel.getWeather(), WeatherActivity.WEATHER_BG_UPDATE_MESSAGE);
		((WeatherActivity)getActivity()).sendMessage(weatherAndCityModel, WeatherActivity.WEATHER_ACTIVITY_DATA_UPDATE);
	}
}
