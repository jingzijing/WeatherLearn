package com.jzj.weatherlearn.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.jzj.weatherlearn.R;
import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.model.City;
import com.jzj.weatherlearn.ui.adapter.base.BaseRecyclerViewAdapter;

import java.util.List;

public class CityManageRecyclerViewAdapter extends BaseRecyclerViewAdapter<City, CityManageRecyclerViewAdapter.CityManageRecyclerViewHolder>
{
	public CityManageRecyclerViewAdapter(@NonNull Context context, @NonNull List<City> datas)
	{
		super(context, datas);
	}

	@NonNull
	@Override
	public CityManageRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.city_manage_recyclerview_item, parent, false);
		return new CityManageRecyclerViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(@NonNull CityManageRecyclerViewHolder holder, int position)
	{
		City city = mDatas.get(position);
		holder.getBinding().setVariable(BR.city, city);
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	public class CityManageRecyclerViewHolder extends RecyclerView.ViewHolder
	{

		private ViewDataBinding binding;

		public CityManageRecyclerViewHolder(@NonNull ViewDataBinding binding)
		{
			super(binding.getRoot());
			this.binding = binding;
		}

		public ViewDataBinding getBinding()
		{
			return binding;
		}
	}

}

