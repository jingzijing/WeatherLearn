package com.jzj.weatherlearn.util;

import com.jzj.weatherlearn.model.gson_weather.Temperature;

public class TempratureUtil
{

	/**
	 * type 为 'date' ,返回日期部分
	 * type 为 'temp', 返回温度范围部分
	 */
	public static String handleTemperature(Temperature temperature, String type)
	{
		if (temperature == null) return null;

		switch (type)
		{
			case "date":
				return temperature.date.substring(5, 10);
			case "temp":
				return temperature.min + " ~ " + temperature.max + "℃";
			default:
				return null;
		}
	}

	/**
	 * keep 指 保留几位小数
	 */
	public static String subTemperature(Float temperature, int keep)
	{
		String s = String.valueOf(temperature);
		return s.substring(0, s.indexOf(".") + keep) + "℃";
	}

}
