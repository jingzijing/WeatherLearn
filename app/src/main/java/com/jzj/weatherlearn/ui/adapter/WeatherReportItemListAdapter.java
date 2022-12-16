package com.jzj.weatherlearn.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.model.WeatherListModel;
import com.jzj.weatherlearn.ui.adapter.base.BaseRecyclerViewAdapter;

import java.util.List;

public class WeatherReportItemListAdapter extends BaseRecyclerViewAdapter<WeatherListModel, WeatherReportItemListAdapter.WeatherReportItemListViewHolder>
{
	public WeatherReportItemListAdapter(@NonNull Context context, @NonNull List<WeatherListModel> datas)
	{
		super(context, datas);
	}

	@NonNull
	@Override
	public WeatherReportItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.weather_show_report_recyclerview_item, parent, false);
		WeatherReportItemListViewHolder viewHolder = new WeatherReportItemListViewHolder(binding);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull WeatherReportItemListViewHolder holder, int position)
	{
		WeatherListModel weatherListModel = mDatas.get(position);
		holder.getBinding().setVariable(BR.weatherListModel, weatherListModel);
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	public class WeatherReportItemListViewHolder extends RecyclerView.ViewHolder
	{

		private ViewDataBinding binding;

		public WeatherReportItemListViewHolder(@NonNull ViewDataBinding binding)
		{
			super(binding.getRoot());
			this.binding = binding;
		}

		public ViewDataBinding getBinding() {
			return binding;
		}
	}
}

