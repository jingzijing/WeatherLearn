package com.jzj.weatherlearn.ui;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.WeatherListModel;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.tool.SkyconUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 天气数据展示
 */
public class WeatherFragment extends Fragment {

    //更新UI数据的消息标识
    public static final int WEATHER_ON_RESPONSE = 0;
    public static final int WEATHER_ON_NETWORD_REQUEST = 1;

    private String TAG;
    private int mCityCode;
    //butterknife
    private Unbinder mUnbinder;
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
    @BindView(R.id.weather_skycon)
    TextView weatherSkycon;
    @BindView(R.id.weather_advice)
    TextView weatherAdvice;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.weather_report_container)
    RecyclerView recyclerView;
    private List<WeatherListModel> mWeatherList;
    private WeatherReportItemListAdapter mWeatherReportItemListAdapter;
    //接口由 WeatherActivity提供
    private ActivityCallback mWeatherActivityWeatherBgCallBack;
    private boolean refreshFlag;
    private Handler mHandler = new WeatherFragmentHandler();

    class WeatherFragmentHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                /**
                 * 更新画面数据
                 */
                case WEATHER_ON_RESPONSE:
                    HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
                    if (map != null) {
                        Weather weather = (Weather) map.get("weather");
                        City city = (City) map.get("city");
                        updateToView(weather, city);
                        if (refreshFlag) {
                            Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
                            refreshFlag = false;
                        }
                    }
                    break;
                /**
                 * 刷新天气信息
                 */
                case WEATHER_ON_NETWORD_REQUEST:
                    if (mWeatherActivityWeatherBgCallBack != null) {
                        mWeatherActivityWeatherBgCallBack.loadWeatherInfoWithNetwork(mCityCode);
                        refreshFlag = true;
                    }
                    break;
            }
        }
    }

    /**
     * 构造函数
     */
    public WeatherFragment(int mCityCode, ActivityCallback callback) {
        this.mCityCode = mCityCode;
        this.mWeatherActivityWeatherBgCallBack = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather_fragment, container, false);
        /**
         * butterknife初始化
         */
        mUnbinder = ButterKnife.bind(this, view);
        /**
         * 初始化
         */
        init();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * weather temp透明度及字体颜色为白
         */
        weatherTempCardview.getBackground().setAlpha(60);
        //scrollview初始化不可见,直至有数据出现
        scrollView.setVisibility(ScrollView.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        mWeatherActivityWeatherBgCallBack.loadWeatherInfo(mCityCode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
    }

    /**
     * 初始化
     */
    private void init() {
        childViewAlphaSetFlag = false;
        TAG = "WeatherFragment";
        mWeatherList = new ArrayList<>();
    }

    /**
     * 提供给外部创建fragment
     */
    public static WeatherFragment newInstance(int cityIndex, ActivityCallback callback) {
        if (cityIndex < 0) {
            return null;
        }
        return new WeatherFragment(cityIndex, callback);
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

    /**
     * 更新数据到控件中
     */
    private void updateToView(Weather weather, City city) {
        if (weather != null) {
            scrollView.setVisibility(ScrollView.VISIBLE);
        }
        /**
         * 设置透明度
         */
        if (!childViewAlphaSetFlag) {
            childViewAlphaSetFlag = true;
            //setScrollChildAlpha(0);
        }
        /**
         * 当天温度
         */
        String avgTempFrom = String.valueOf(weather.result.daily.temperature.get(0).avg);
        String avgTemp = avgTempFrom.substring(0, avgTempFrom.indexOf(".") + 2) + "℃";
        weatherTemp.setText(avgTemp);
        weatherCityName.setText(city.getCityName());
        weatherToday.setText(weather.result.daily.temperature.get(0).date.substring(5, 10));
        weatherSkycon.setText(SkyconUtil.getSkyConName(weather.result.daily.skycon.get(0).value));
        /**
         *  更新天气预报列表数据
         */
        mWeatherList = weather.getResultDailyWeatherModel();
        mWeatherReportItemListAdapter.notifyDataSetChanged();
        /**
         * 生活建议
         */
        String advice = SkyconUtil.getWeatherAdvice(weather);
        if (advice == null || "".equals(advice)) {
            weatherAdviceCardview.setVisibility(CardView.INVISIBLE);
        } else {
            weatherAdvice.setText(advice);
        }
    }

    /**
     * 设置内容透明度，除了温度、城市信息固定透明度，其他子view的透明度都由参数alpha决定
     */
    private void setScrollChildAlpha(int alpha) {
        LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        int childCount = linearLayout.getChildCount();
        for (int i = 1; i < childCount; i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof ViewGroup) {
                view.getBackground().setAlpha(0);
            }
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
            String itemTemperature = weatherListModel.temperature.min + " ~ " + weatherListModel.temperature.max + "℃";
            holder.weatherItemDateText.setText(date);
            holder.weatherItemTempratureText.setText(itemTemperature);
            holder.weatherItemSkyconText.setText(SkyconUtil.getSkyConName(weatherListModel.skycon.value));
            //图标
            Drawable icno = SkyconUtil.handlerWeatherIcno(weatherListModel.skycon.value);
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
        TextView weatherItemSkyconText;
        TextView weatherItemTempratureText;

        public WeatherReportItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            weatherItemDateText = itemView.findViewById(R.id.weather_item_date_text);
            weatherItemIcon = itemView.findViewById(R.id.weather_item_icon);
            weatherItemSkyconText = itemView.findViewById(R.id.weather_item_skycon);
            weatherItemTempratureText = itemView.findViewById(R.id.weather_item_temprature_text);
        }
    }

    public int getCityCode() {
        return mCityCode;
    }
}
