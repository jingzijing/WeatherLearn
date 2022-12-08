package com.jzj.weatherlearn.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;

import java.util.List;

public class CityManageActivity extends AppCompatActivity {

    private final static String MENU_TITLE = "城市管理";
    private String TAG = CityManageActivity.class.getName();
    private RecyclerView recyclerView;
    private List<City> mCityList;
    private CityManageRecyclerViewAdapter adapter;
    //城市列表是否数据变动
    private boolean dataChangedFlag = false;
    private Handler mHandler = new Handler();
    private Context mContext = this;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manage);
        /**
         * actionbar
         */
        toolbar = findViewById(R.id.city_manager_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.city_manage_toolbar_customerview);
            TextView titleText = actionBar.getCustomView().findViewById(R.id.city_manage_toolbar_title);
            titleText.setText(MENU_TITLE);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        /**
         * recyclerview
         */
        mCityList = CitySetting.getInstance().getCacheCities();
        recyclerView = findViewById(R.id.city_manage_recyclerview);
        adapter = new CityManageRecyclerViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerview条目滑动监听
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (CitySetting.getInstance().getCacheCities().size() > 1) {
                    int position = viewHolder.getAdapterPosition();
                    City city = mCityList.get(position);
                    CitySetting.getInstance().deleteCity(city);
                    mCityList = CitySetting.getInstance().getCacheCities();
                    adapter.notifyItemRemoved(position);

                    if (!dataChangedFlag)
                        dataChangedFlag = true;
                }
            }
        });
        touchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manager_city, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //返回键
            case android.R.id.home:
                finish();
                return true;
            //清空城市缓存
            case R.id.city_remove_all:
                requestRemoveAllCityCache();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CityManageRecyclerViewAdapter extends RecyclerView.Adapter<CityManageRecyclerViewHolder> {
        @NonNull
        @Override
        public CityManageRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(App.context).inflate(R.layout.city_manage_recyclerview_item, parent, false);
            return new CityManageRecyclerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CityManageRecyclerViewHolder holder, int position) {
            City city = mCityList.get(position);
            holder.cityName.setText(city.getCityName());
        }

        @Override
        public int getItemCount() {
            return mCityList.size();
        }
    }

    private class CityManageRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView cityName;

        public CityManageRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_manage_city_name);
        }
    }

    /**
     * 确认是否删除所有城市缓存
     */
    private void requestRemoveAllCityCache() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(mContext)
                        .setTitle("确认删除所有城市缓存")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CitySetting.getInstance().removeAllCache();
                                mCityList.clear();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }


}
