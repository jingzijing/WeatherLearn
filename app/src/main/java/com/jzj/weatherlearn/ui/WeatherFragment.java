package com.jzj.weatherlearn.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.global.SharedPreferencesManager;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.CityAndWeather;
import com.jzj.weatherlearn.model.WeatherListModel;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.viewmodel.WeatherAndCityViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 天气数据展示
 */
public class WeatherFragment extends Fragment {

    private String TAG = WeatherFragment.class.getName();
    private SharedPreferences sharedPreferences;
    //更新UI数据的消息标识
    public static final int WEATHER_ON_RESPONSE = 2;
    // 使用newInstance时由外部提供
    private int mWeatherIndex;
    //butterknife
    private Unbinder mUnbinder;

    class WeatherFragmentHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新画面数据
                case WEATHER_ON_RESPONSE:
                    Weather weather = (Weather) msg.obj;
                    if (weatherAndCityViewModel.getCityAndWeatherLiveData().getValue() != null) {
                        updateToView();
                    }
                    break;
            }
        }
    }

    private Handler mHandler = new WeatherFragmentHandler();
    //当前fragment展示的city相关的天气数据
    private City nowCity;
    //仅设置1次ui透明度的标识
    private boolean childViewAlphaSetFlag;
    //控件绑定
    @BindView(R.id.weather_temp_cardview)
    CardView weatherTempCardview;
    @BindView(R.id.weather_advice_container)
    CardView weatherAdviceCardview;
    @BindView(R.id.weather_temp)
    TextView weatherTemp;
    @BindView(R.id.weather_city_name)
    TextView weatherCityName;
    @BindView(R.id.weather_today)
    TextView weatherToday;
    @BindView(R.id.weather_advice)
    TextView weatherAdvice;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.weather_report_container)
    RecyclerView recyclerView;
    private List<WeatherListModel> mWeatherList = new ArrayList<>();
    private WeatherReportItemListAdapter mWeatherReportItemListAdapter;
    //回调接口由 WeatherActivity提供,功能是根据api提供的skycon类型改变背景图片
    private ActivityCallback mWeatherActivityWeatherBgCallBack;
    private WeatherAndCityViewModel weatherAndCityViewModel;

    /**
     * mWeatherIndex和 缓存城市列表下标 相同
     *
     * @param mWeatherIndex
     */
    public WeatherFragment(int mWeatherIndex) {
        this.mWeatherIndex = mWeatherIndex;
        this.nowCity = CitySetting.getInstance().getCacheCities(sharedPreferences).get(mWeatherIndex);
    }

    public WeatherFragment(int mWeatherIndex, ActivityCallback callback) {
        this.mWeatherIndex = mWeatherIndex;
        this.nowCity = CitySetting.getInstance().getCacheCities(sharedPreferences).get(mWeatherIndex);
        this.mWeatherActivityWeatherBgCallBack = callback;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = SharedPreferencesManager.getSharedPreferences(TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        childViewAlphaSetFlag = false;
        weatherAndCityViewModel = new ViewModelProvider(this).get(WeatherAndCityViewModel.class);
        weatherAndCityViewModel.getCityAndWeatherLiveData().observe(getViewLifecycleOwner(), new Observer<CityAndWeather>() {
            @Override
            public void onChanged(CityAndWeather cityAndWeather) {
                //更新UI消息
                sendWeatherInfoToHandler(cityAndWeather.weather, mHandler, WEATHER_ON_RESPONSE);
                //更新背景图消息
                mWeatherActivityWeatherBgCallBack.handleWeatherBg(cityAndWeather.weather, WeatherActivity.WEATHER_BG_UPDATE_MESSAGE);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        /**
         * weather temp透明度及字体颜色为白
         */
        weatherTempCardview.getBackground().setAlpha(185);
        weatherTemp.setTextColor(Color.BLACK);
        weatherCityName.setTextColor(Color.BLACK);
        weatherToday.setTextColor(Color.BLACK);
        //scrollview初始化不可见,直至有数据出现
        scrollView.setVisibility(ScrollView.INVISIBLE);
        /**
         * recyclerview
         */
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWeatherReportItemListAdapter = new WeatherReportItemListAdapter();
        recyclerView.setAdapter(mWeatherReportItemListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取天气信息,并将信息展示
        weatherAndCityViewModel.getWeatherInfo(nowCity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferencesManager.getSharedPreferences(TAG);
    }

    /**
     * 更新数据到控件中
     */
    private void updateToView() {
        Weather weather = weatherAndCityViewModel.getCityAndWeatherLiveData().getValue().weather;
        City city = weatherAndCityViewModel.getCityAndWeatherLiveData().getValue().city;
        if (weather != null) {
            scrollView.setVisibility(ScrollView.VISIBLE);
        }
        /**
         * 设置透明度
         */
        if (!childViewAlphaSetFlag) {
            childViewAlphaSetFlag = true;
            //view.setAlpha方法，参数范围0-1
            setScrollChildAplha((float) (0.75));
        }
        /**
         * 当天温度
         */
        String avgTempFrom = String.valueOf(weather.result.daily.temperature.get(0).avg);
        String avgTemp = avgTempFrom.substring(0, avgTempFrom.indexOf(".") + 2) + "℃";
        String today = weather.result.daily.temperature.get(0).date.substring(5, 10);
        weatherTemp.setText(avgTemp);
        weatherCityName.setText(city.getCityName());
        weatherToday.setText(today);
        /**
         *  更新天气预报列表数据
         */
        mWeatherList = weather.getResultDailyWeatherModel();
        mWeatherReportItemListAdapter.notifyDataSetChanged();
        /**
         * 生活建议
         */
        String advice = weatherAndCityViewModel.getWeatherAdvice(weather);
        if (advice == null || "".equals(advice)) {
            weatherAdviceCardview.setVisibility(CardView.INVISIBLE);
        } else {
            weatherAdvice.setText(advice);
        }
    }

    //提供给外部创建, fragment初始化从citysetting里面获取城市信息
    public static WeatherFragment newInstance(int cityIndex) {
        if (cityIndex < 0) {
            return null;
        }
        return new WeatherFragment(cityIndex);
    }

    //提供给外部创建, fragment初始化从citysetting里面获取城市信息
    public static WeatherFragment newInstance(int cityIndex, ActivityCallback callback) {
        if (cityIndex < 0) {
            return null;
        }
        return new WeatherFragment(cityIndex, callback);
    }

    /**
     * 发送天气信息更新UI的消息
     *
     * @param weather
     */
    public static void sendWeatherInfoToHandler(Weather weather, Handler handler, int messageWhat) {
        Message message = Message.obtain();
        message.what = messageWhat;
        message.obj = weather;
        handler.sendMessage(message);
    }

    /**
     * 设置scroolerview内容透明度，除了温度、城市信息固定透明，其他子view的透明度都由参数alpha决定
     *
     * @param alpha
     */
    private void setScrollChildAplha(Float alpha) {
        LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        int childCount = linearLayout.getChildCount();
        for (int i = 1; i < childCount; i++) {
            linearLayout.getChildAt(i).setAlpha(alpha);
        }
    }

    /**
     * 天气预报列表的适配器
     */
    private class WeatherReportItemListAdapter extends RecyclerView.Adapter<WeatherReportItemListViewHolder> {

        @NonNull
        @Override
        public WeatherReportItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.weather_report_item, parent, false);
            WeatherReportItemListViewHolder viewHolder = new WeatherReportItemListViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherReportItemListViewHolder holder, int position) {
            WeatherListModel weatherListModel = mWeatherList.get(position);
            //截取日月
            String date = weatherListModel.temperature.date.substring(5, 10);
            holder.weatherItemDateText.setText(date);
            holder.weatherItemMinTempratureText.setText(String.valueOf(weatherListModel.temperature.min) + "℃");
            holder.weatherItemMaxTempratureText.setText(String.valueOf(weatherListModel.temperature.max) + "℃");
            //图标
            Drawable icno = weatherAndCityViewModel.handlerWeatherIcno(weatherListModel.skycon.value);
            holder.weatherItemIcon.setImageDrawable(icno);
        }

        @Override
        public int getItemCount() {
            return mWeatherList.size();
        }
    }

    /**
     * 天气预报列表需要的viewholder
     */
    private class WeatherReportItemListViewHolder extends RecyclerView.ViewHolder {

        TextView weatherItemDateText;
        ImageView weatherItemIcon;
        TextView weatherItemMinTempratureText;
        TextView weatherItemMaxTempratureText;

        public WeatherReportItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            weatherItemDateText = itemView.findViewById(R.id.weather_item_date_text);
            weatherItemIcon = itemView.findViewById(R.id.weather_item_icon);
            weatherItemMinTempratureText = itemView.findViewById(R.id.weather_item_min_temprature_text);
            weatherItemMaxTempratureText = itemView.findViewById(R.id.weather_item_max_temprature_text);
        }
    }
}
