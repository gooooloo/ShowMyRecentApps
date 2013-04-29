/*
 * Copyright 2013 Qidu Lin
 * 
 * This file is part of ShowMyRecentApps.
 * 
 * ShowMyRecentApps is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ShowMyRecentApps is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ShowMyRecentApps. If not, see <http://www.gnu.org/licenses/>.
 */

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