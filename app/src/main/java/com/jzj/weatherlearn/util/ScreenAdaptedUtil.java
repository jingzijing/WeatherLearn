package com.jzj.weatherlearn.util;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * 屏幕适配工具
 */
public class ScreenAdaptedUtil
{

	public static void widthAutoAdaptedScreen(@NonNull Activity activity, @NonNull Application application)
	{

		final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
		final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();

		final float targetDensity = appDisplayMetrics.widthPixels / 360;
		final int targetDensityDpi = (int) (targetDensity * 160);
		Log.d("===", "设备宽度(px) : " + appDisplayMetrics.widthPixels);
		Log.d("===", "计算出的density : " + targetDensity);

		appDisplayMetrics.scaledDensity = targetDensity * (appDisplayMetrics.scaledDensity / appDisplayMetrics.density);
		appDisplayMetrics.density = targetDensity;
		appDisplayMetrics.densityDpi = targetDensityDpi;

		activityDisplayMetrics.scaledDensity = targetDensity * (activityDisplayMetrics.scaledDensity / activityDisplayMetrics.density);
		activityDisplayMetrics.density = targetDensity;
		activityDisplayMetrics.densityDpi = targetDensityDpi;

	}

}
