// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

class AppStatisticsManager
{
	final static String KEY = "DF";
	private static Map<String, Integer> map = null;

	public static AppStatisticsManager getInstance(Context context)
	{
		return new AppStatisticsManager(context);
	}

	final Context context;

	private AppStatisticsManager(Context context)
	{
		this.context = context;
	}

	public void AddAppUsdCountByOne(String packageName)
	{
		getAll();
		int newCount = map.containsKey(packageName) ? map.get(packageName) + 1 : 1;
		map.put(packageName, newCount);
		setCount(packageName, newCount);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Integer> getAll()
	{
		if (map == null)
		{
			map = (Map<String, Integer>) getSp().getAll();
		}

		return map;
	}

	private SharedPreferences getSp()
	{
		return context.getSharedPreferences(KEY, ShowGetRecentAppsActivity.MODE_PRIVATE);
	}

	private void setCount(String className, int count)
	{
		getSp().edit().putInt(className, count).commit();
	}
}