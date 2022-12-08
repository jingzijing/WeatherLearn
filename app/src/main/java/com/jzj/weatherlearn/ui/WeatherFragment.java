package com.jzj.weatherlearn.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.model.WeatherListModel;
import com.jzj.weatherlearn.model.Weather;
import com.jzj.weatherlearn.tool.SkyconUtil;
import com.jzj.weatherlearn.viewmodel.WeatherAndCityViewModel;

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

    private String TAG = "WeatherFragment";
    private WeatherAndCityViewModel viewModel;
    private int mCityCode;
    //butterknife
    private Unbinder mUnbinder;
    //仅设置1次ui透明度的标识
    private boolean childViewAlphaSetFlag = false;
    //控件绑定
    @BindView(R.id.weather_advice_container)
    CardView weatherAdviceCardview;
    @BindView(R.id.weather_advice)
    TextView weatherAdvice;
    @BindView(R.id.weather_report_container)
    RecyclerView recyclerView;
    @BindView(R.id.sunrise_text_view)
    TextView sunriseTextView;
    @BindView(R.id.sunset_text_view)
    TextView sunsetTextView;
    private List<WeatherListModel> mWeatherList = new ArrayList<>();
    private WeatherReportItemListAdapter mWeatherReportItemListAdapter;
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
                    if (!refreshFlag) {
                        refreshFlag = true;
                        viewModel.getWeatherInfoFromNetwork(viewModel.findCityByCode(mCityCode));
                    }
                    break;
            }
        }
    }

    /**
     * 构造函数
     */
    public WeatherFragment(int mCityCode) {
        this.mCityCode = mCityCode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(WeatherAndCityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        /**
         * butterknife初始化
         */
        mUnbinder = ButterKnife.bind(this, view);
        return view;
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
        viewModel.getWeatherInfo(viewModel.findCityByCode(mCityCode));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
    }

    /**
     * 提供给外部创建fragment
     */
    public static WeatherFragment newInstance(int cityCode) {
        return new WeatherFragment(cityCode);
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
        /**
         * 日出日落
         */
        String sunrise = weather.result.daily.astro.get(0).sunrise.time;
        String sunset = weather.result.daily.astro.get(0).sunset.time;
        sunriseTextView.setText(sunrise);
        sunsetTextView.setText(sunset);
    }

    /**
     * 天气预报列表的适配器
     */
    private class WeatherReportItemListAdapter extends RecyclerView.Adapter<WeatherReportItemListViewHolder> {

        @NonNull
        @Override
        public WeatherReportItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.weather_show_report_recyclerview_item, parent, false);
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
