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

package com.qidu.lin.showRecentApps.bg.appInfo;

import java.util.HashSet;
import java.util.Set;

import com.qidu.lin.showRecentApps.fg.RecentAppsActivity;

import android.content.Context;
import android.content.SharedPreferences;

public class AppInfoBlackList
{
	private static Set<String> blackList = null;
	private static SharedPreferences sp = null;

	public static boolean hasBlackList(Context context)
	{

		if (sp == null)
		{
			sp = getSp(context);
		}

		if (blackList == null)
		{
			blackList = getBlackList();
		}

		return blackList != null;
	}

	private static Set<String> getBlackList()
	{
		return parseListString(sp.getString("packageNames", null));
	}

	private static SharedPreferences getSp(Context context)
	{
		return context.getSharedPreferences("blacklist", RecentAppsActivity.MODE_PRIVATE);
	}

	public static boolean isInBlackList(String packageName)
	{

		if (blackList == null)
		{
			return false;
		}

		return blackList.contains(packageName);
	}

	private static String makeListString(Set<String> packageNames)
	{
		StringBuffer is = new StringBuffer();
		boolean firstOne = true;
		for (String name : packageNames)
		{
			if (!firstOne)
			{
				is.append("#");
			}
			firstOne = false;
			is.append(name);
		}
		return is.toString();
	}

	private static Set<String> parseListString(String str)
	{
		if (str == null)
		{
			return null;
		}

		HashSet<String> yy = new HashSet<String>();
		for (String xx : str.split("#"))
		{
			yy.add(xx);
		}
		return yy;

	}

	public static void setBlackList(Set<String> packageNames, Context context)
	{
		sp.edit().putString("packageNames", makeListString(packageNames)).commit();
	}
	
	public static void addToBlackList(String packageName, Context context)
	{
		Set<String> blacklist = getBlackList();
		blacklist.add(packageName);
		setBlackList(blacklist, context);
	}
}
