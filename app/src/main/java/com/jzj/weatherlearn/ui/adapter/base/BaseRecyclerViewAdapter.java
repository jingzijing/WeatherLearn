package com.jzj.weatherlearn.ui.adapter.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.jzj.weatherlearn.model.City;

import java.util.List;

public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
{
	protected List<T> mDatas;
	protected Context mContext;

	public BaseRecyclerViewAdapter(@NonNull Context context, @NonNull List<T> datas)
	{
		mDatas = datas;
		mContext = context;
	}

	public void setDatas(List<T> datas)
	{
		if (datas == null) return;
		mDatas.clear();
		mDatas.addAll(datas);
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

}
