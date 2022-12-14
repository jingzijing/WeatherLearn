package com.jzj.weatherlearn.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.databinding.ActivityCitySelectBinding;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.ui.base.BaseActivity;
import com.jzj.weatherlearn.util.KeyBordUtil;
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
public class CitySelectActivity extends BaseActivity<ActivityCitySelectBinding>
{

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
    private CityViewModel cityViewModel;
    private int mCityLevel;
    private List<City> mCityList;
    private LocationManager mLocationManager;
    private Context mContext;

    private NiceSpinner cityLevelSpinner;
    private EditText citySearchEdit;
    private RecyclerView cityContainer;
    private CitySelectRecyclerViewAdapter adapter;
    private Button locationBtn;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        /**
         * actionbar
         */
        toolbar = findViewById(R.id.city_selected_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.city_manage_toolbar_customerview);
            TextView titleText = actionBar.getCustomView().findViewById(R.id.city_manage_toolbar_title);
            titleText.setText(MENU_TITLE);
            if (CitySetting.getInstance().getCachesCitiesSize() > 0) {
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
                addCityAndJump(city);
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
        /**
         * 定位button
         */
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationBtn = findViewById(R.id.location_btn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭edittext软键盘
                KeyBordUtil.closeKeyBordUtil(citySearchEdit);
                //授权后定位
                if (requestPermission()) {
                    try {
                        //检查定位功能是否打开
                        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            displayToast("请确保定位功能和网络功能已打开");
                            return;
                        }
                        locationBtn.setText("定位中...");
                        locationBtn.setEnabled(false);
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                mLocationManager.removeUpdates(this);
                                //viewmodel获取城市名并回调
                                cityViewModel.getCityByLatAndLon(location, new CityViewModel.OnNetworkResponseListener() {
                                    @Override
                                    public void onResponse(City city) {
                                        addCityAndJump(city);
                                    }

                                    @Override
                                    public void onFailure() {
                                        displayToast("定位失败,请手动选择城市");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                locationBtn.setText("重新定位");
                                                locationBtn.setEnabled(true);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
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

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_city_select;
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
     * 查看权限,如果没有则获取
     */
    private boolean requestPermission() {
        boolean result = false;
        if (XXPermissions.isHasPermission(mContext, Permission.ACCESS_COARSE_LOCATION) && XXPermissions.isHasPermission(mContext, Permission.ACCESS_FINE_LOCATION)) {
            result = true;
        } else {
            XXPermissions.with((Activity) mContext)
                    .permission(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {

                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            if (quick) {
                                displayToast("请手动打开定位权限");
                                XXPermissions.gotoPermissionSettings(mContext);
                            } else {
                                displayToast("定位功能需要授权");
                            }
                        }
                    });
            if (XXPermissions.isHasPermission(mContext, Permission.ACCESS_COARSE_LOCATION) && XXPermissions.isHasPermission(mContext, Permission.ACCESS_FINE_LOCATION)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * toast
     */
    private void displayToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 添加city并跳转
     */
    private void addCityAndJump(City city) {
        boolean canAdd = true;
        List<City> cityList = CitySetting.getInstance().getCacheCities();
        for (City c : cityList) {
            if (c.getCityCode() == city.getCityCode()) {
                canAdd = false;
                break;
            }
        }
        if (canAdd) {
            CitySetting.getInstance().addCity(city);
        } else {
            displayToast("已存在" + city.getCityName() + "的天气信息");
        }
        Intent intent = new Intent(CitySelectActivity.this, WeatherActivity.class);
        startActivity(intent);
        finish();
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
            View itemView = LayoutInflater.from(App.context).inflate(R.layout.city_selected_recyclerview_item, parent, false);
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