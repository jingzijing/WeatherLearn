package com.jzj.weatherlearn.ui.base;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.jzj.weatherlearn.util.ScreenAdaptedUtil;

public abstract class BaseActivity<VB extends ViewDataBinding> extends AppCompatActivity
{

	protected VB viewDataBinding;

	protected String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ScreenAdaptedUtil.widthAutoAdaptedScreen(this, getApplication());
		if (viewDataBinding == null)
		{
			viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
		}
	}

	protected abstract int getLayoutId();


}
