package com.jzj.weatherlearn.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.global.SharedPreferencesManager;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.tool.KeyBordUtil;
import com.jzj.weatherlearn.viewmodel.CityViewModel;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 城市选择界面
 */
public class CitySelectActivity extends AppCompatActivity {

    public static final int CITY_SELECT_ACTIVITY_FINISH_MESSAGE = 1;
    private final String MENU_TITLE = "城市列表";

    class mHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CITY_SELECT_ACTIVITY_FINISH_MESSAGE:
                    finish();
            }
        }
    }

    private Handler mHandler = new mHandler();
    private String TAG = CitySelectActivity.class.getName();
    private CityViewModel cityViewModel;
    private int mCityLevel;
    private List<City> mCityList;
    private SharedPreferences sharedPreferences;

    private NiceSpinner cityLevelSpinner;
    private EditText citySearchEdit;
    private RecyclerView cityContainer;
    private CitySelectRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        /**
         * sharedPreferences
         */
        sharedPreferences = SharedPreferencesManager.getSharedPreferences(TAG);
        /**
         * actionbar
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.activity_city_manage_toolbar_customerview);
            TextView titleText = actionBar.getCustomView().findViewById(R.id.city_manage_toolbar_title);
            titleText.setText(MENU_TITLE);
            if (CitySetting.getInstance().getCacheCities(sharedPreferences).size() > 0) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowCustomEnabled(true);
            }
        }
        /**
         * RecyclerView
         */
        cityContainer = findViewById(R.id.city_container);
        //默认列出省级城市
        mCityLevel = CitySetting.CITY_LEVEL_PROVINCE;
        mCityList = new ArrayList<>();
        adapter = new CitySelectRecyclerViewAdapter();
        //item监听
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //选中城市放入全局cacheCities的第一位
                City city = mCityList.get(position);
                CitySetting.getInstance().addCity(city, sharedPreferences);
                Intent intent = new Intent(CitySelectActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //绑定viewModel
        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        cityViewModel.getCityLiveData().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(List<City> cityList) {
                mCityList.clear();
                mCityList.addAll(cityList);
                adapter.notifyDataSetChanged();
            }
        });
        updateRecyclerViewData(mCityLevel, null);
        cityContainer.setLayoutManager(new LinearLayoutManager(this));
        cityContainer.setAdapter(adapter);
        //点击城市列表,关闭edittext呼出的软键盘
        cityContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyBordUtil.closeKeyBordUtil(citySearchEdit);
                return false;
            }
        });
        /**
         * 选择城市等级 spinner
         */
        cityLevelSpinner = findViewById(R.id.city_level_spinner);
        LinkedList<String> spinnerItem = new LinkedList<>(Arrays.asList(App.context.getResources().getStringArray(R.array.city_level_array)));
        cityLevelSpinner.attachDataSource(spinnerItem);
        cityLevelSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                String switchSelect = App.context.getResources().getStringArray(R.array.city_level_array)[position];
                String province = App.context.getResources().getString(R.string.province);
                String city = App.context.getResources().getString(R.string.city);
                String district = App.context.getResources().getString(R.string.district);
                if (switchSelect.equals(province)) {
                    mCityLevel = CitySetting.CITY_LEVEL_PROVINCE;
                } else if (switchSelect.equals(city)) {
                    mCityLevel = CitySetting.CITY_LEVEL_CITY;
                } else if (switchSelect.equals(district)) {
                    mCityLevel = CitySetting.CITY_LEVEL_DISTRICT;
                }
                updateRecyclerViewData(mCityLevel, citySearchEdit.getText() != null ? citySearchEdit.getText().toString() : null);
            }
        });
        /**
         * 城市搜索栏 editText
         */
        citySearchEdit = findViewById(R.id.city_search_edit);
        citySearchEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        citySearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (citySearchEdit != null && !("".equals(citySearchEdit))) {
                    updateRecyclerViewData(mCityLevel, citySearchEdit.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesManager.itemDestroy(TAG);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //返回键
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 更新城市列表数据到ui
     */
    private void updateRecyclerViewData(Integer cityLevel, String searchCity) {
        if (searchCity != null && !"".equals(searchCity)) {
            cityViewModel.getCityLiveDataWithLevel(cityLevel, searchCity);
        } else {
            cityViewModel.getCityLiveDataWithLevel(cityLevel);
        }
    }

    /**
     * Recyclerview item监听
     */
    interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * RecyclerView.adapter
     */
    class CitySelectRecyclerViewAdapter extends RecyclerView.Adapter<CitySelectRecyclerViewHolder> {

        private OnItemClickListener mOnItemClickListener;

        @NonNull
        @Override
        public CitySelectRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(App.context).inflate(R.layout.city_select_item, parent, false);
            return new CitySelectRecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CitySelectRecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.cityNameItem.setText(mCityList.get(position).getCityName());
            if (mOnItemClickListener != null) {
                holder.cityItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mCityList.size();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }
    }

    /**
     * RecyclerView.ViewHolder
     */
    class CitySelectRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView cityNameItem;
        LinearLayout cityItemLayout;

        public CitySelectRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            cityNameItem = itemView.findViewById(R.id.city_name_item);
            cityItemLayout = itemView.findViewById(R.id.city_item_layout);
        }
    }

}