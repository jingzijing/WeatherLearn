package com.jzj.weatherlearn.ui.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import java.lang.ref.WeakReference;


public abstract class BaseLazyFragment<VB extends ViewDataBinding, VM extends ViewModel> extends Fragment
{
	protected VB viewDataBinding;

	protected VM viewModel;

	protected String TAG = this.getClass().getSimpleName();

	protected View rootView = null;

	protected boolean isViewCreated = false;

	protected boolean mIsFirstVisible = true;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		if (rootView == null)
		{
			viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
			rootView = viewDataBinding.getRoot();
		}
		isViewCreated = true;
		return rootView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		viewModel = getViewModel();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (isViewCreated && mIsFirstVisible && !isHidden())
		{
			loadData();
			mIsFirstVisible = false;
		}
	}

	protected abstract VM getViewModel();

	protected abstract int getLayoutId();

	protected abstract void loadData();

}
