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
import com.jzj.weatherlearn.databinding.ActivityCityManageBinding;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.global.CitySetting;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.ui.adapter.CityManageRecyclerViewAdapter;
import com.jzj.weatherlearn.ui.base.BaseActivity;

import java.util.List;

public class CityManageActivity extends BaseActivity<ActivityCityManageBinding>
{
	private final static String MENU_TITLE = "城市管理";
	private RecyclerView recyclerView;
	private List<City> mCityList;
	private CityManageRecyclerViewAdapter adapter;
	private Context mContext = this;
	private Toolbar toolbar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/**
		 * actionbar
		 */
		toolbar = findViewById(R.id.city_manager_toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionBar.setCustomView(R.layout.city_manage_toolbar_customerview);
			TextView titleText = actionBar.getCustomView()
					.findViewById(R.id.city_manage_toolbar_title);
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
		adapter = new CityManageRecyclerViewAdapter(this, mCityList);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(layoutManager);
		//recyclerview条目滑动监听
		ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT)
		{
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
			{
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
			{
				if (CitySetting.getInstance().getCacheCities().size() > 1)
				{
					int position = viewHolder.getAdapterPosition();
					City city = mCityList.get(position);
					CitySetting.getInstance().deleteCity(city);
					mCityList = CitySetting.getInstance().getCacheCities();
					adapter.notifyItemRemoved(position);
				}
			}
		});
		touchHelper.attachToRecyclerView(recyclerView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_manager_city, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		switch (item.getItemId())
		{
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

	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_city_manage;
	}

	/**
	 * 确认是否删除所有城市缓存
	 */
	private void requestRemoveAllCityCache()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				new AlertDialog.Builder(mContext).setTitle("确认删除所有城市缓存")
						.setPositiveButton("确定", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
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
