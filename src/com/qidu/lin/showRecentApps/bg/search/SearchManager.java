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

package com.qidu.lin.showRecentApps.bg.search;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Pair;

import com.qidu.lin.showRecentApps.bg.PinYinBridge;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoItem;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoManager;
import com.qidu.lin.showRecentApps.fgbg.SearchResultListener;
import com.qidu.lin.showRecentApps.fgbg.VirtualAppInfoListUI;

public class SearchManager
{
	private SearchResultListener resultListner = null;

	private SearchManager()
	{
	}

	private static SearchManager instance = new SearchManager();

	public void setSearchResultListener(SearchResultListener listener)
	{
		resultListner = listener;
	}

	public static SearchManager getInstance()
	{
		return instance;
	}

	class SearchAsyncTask extends AsyncTask<String, List<Pair<AppInfoItem, Boolean>>, Void>
	{
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params)
		{
			List<Pair<AppInfoItem, Boolean>> xxx = new ArrayList<Pair<AppInfoItem, Boolean>>();
			for (AppInfoItem each : AppInfoManager.getInstance().getAppInfoList())
			{
				if (this.isCancelled())
				{
					return null;
				}
				
				String labelString = each.getLabel().toString();
				boolean matched = match(labelString, params[0]);
				xxx.add(new Pair<AppInfoItem, Boolean>(each, matched));
				
				if (xxx.size() >= VirtualAppInfoListUI.getItemCountToShow())
				{
					break;
				}
			}
			this.publishProgress(xxx);
			return null;
		}

		@Override
		protected void onProgressUpdate(List<Pair<AppInfoItem, Boolean>>... values)
		{
			if (resultListner == null)
			{
				return;
			}
			for (List<Pair<AppInfoItem, Boolean>> eachgroup : values)
			{
				if (this.isCancelled())
				{
					return;
				}
				for (Pair<AppInfoItem, Boolean> each : eachgroup)
				{
					if (this.isCancelled())
					{
						return;
					}
					resultListner.onSearchResult(each.first, each.second);
				}
			}
		}

		private boolean match(String packageName, String string)
		{
			if (doSimpleMatch(packageName, string))
			{
				return true;
			}

			for (String xx : PinYinBridge.getHanyuPinyin(packageName))
			{
				if (doSimpleMatch(xx, string))
				{
					return true;
				}
			}
			return false;
		}

		private boolean doSimpleMatch(String packageName, String string)
		{
			char[] aaa = packageName.toLowerCase().toCharArray();
			char[] bbb = string.toLowerCase().toCharArray();

			int iaaa = 0;
			int ibbb = 0;

			while (iaaa < aaa.length && ibbb < bbb.length)
			{
				if (aaa[iaaa] == bbb[ibbb])
				{
					iaaa++;
					ibbb++;
				}
				else
				{
					iaaa++;
				}
			}

			return ibbb == bbb.length;
		}
	}

	SearchAsyncTask searchAsyncTask = null;

	public void onSearch(final String string)
	{
		if (searchAsyncTask != null)
		{
			searchAsyncTask.cancel(true);
		}

		searchAsyncTask = new SearchAsyncTask();
		searchAsyncTask.execute(string);
	}
}
