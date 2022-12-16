package com.jzj.weatherlearn.viewmodel.base;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.lang.ref.WeakReference;

public class BaseViewModel extends AndroidViewModel
{
	Context mContext;

	public BaseViewModel(@NonNull Application application)
	{
		super(application);
		mContext = application.getApplicationContext();
	}

}
