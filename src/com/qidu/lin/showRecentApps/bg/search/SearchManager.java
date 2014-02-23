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

import java.util.Locale;

import android.os.AsyncTask;

import com.qidu.lin.showRecentApps.bg.PinYinBridge;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoItem;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoList;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoManager;
import com.qidu.lin.showRecentApps.fgbg.SearchResultListener;
import com.qidu.lin.showRecentApps.fgbg.VirtualAppInfoListUI;

public class SearchManager
{
	private enum SearchLevel
	{
		loosely, strictly
	}

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

	class SearchAsyncTask extends AsyncTask<String, AppInfoList, Void>
	{
		@Override
		protected Void doInBackground(String... params)
		{
			AppInfoList matchedList = new AppInfoList();
			for (AppInfoItem each : AppInfoManager.getInstance().getAppInfoList())
			{
				if (this.isCancelled())
				{
					return null;
				}

				if (match(each.getLabel().toString(), params[0], SearchLevel.strictly))
				{
					matchedList.add(each);
				}

				if (matchedList.size() >= VirtualAppInfoListUI.getItemCountToShow())
				{
					break;
				}
			}
			for (AppInfoItem each : AppInfoManager.getInstance().getAppInfoList())
			{
				if (this.isCancelled())
				{
					return null;
				}

				if (matchedList.contains(each))
				{
					continue;
				}

				// only for those requently used we enable loosely match. The
				// magic number 10 is a random choosed one.
				if (each.getCount() <= 10)
				{
					continue;
				}

				if (match(each.getLabel().toString(), params[0], SearchLevel.loosely))
				{
					matchedList.add(each);
				}

				if (matchedList.size() >= VirtualAppInfoListUI.getItemCountToShow())
				{
					break;
				}
			}
			this.publishProgress(matchedList);
			return null;
		}

		@Override
		protected void onProgressUpdate(AppInfoList... values)
		{
			if (resultListner == null)
			{
				return;
			}
			for (AppInfoList each : values)
			{
				if (this.isCancelled())
				{
					return;
				}
				resultListner.onSearchResult(each);
			}
		}

		private boolean match(String packageName, String string, SearchLevel searchLevel)
		{
			if (doSimpleMatch(packageName, string, searchLevel))
			{
				return true;
			}

			for (String xx : PinYinBridge.getHanyuPinyin(packageName))
			{
				if (doSimpleMatch(xx, string, searchLevel))
				{
					return true;
				}
			}
			return false;
		}

		private boolean doSimpleMatch(String packageName, String string, SearchLevel searchLevel)
		{
			char[] aaa = packageName.toLowerCase(Locale.getDefault()).toCharArray();
			char[] bbb = string.toLowerCase(Locale.getDefault()).toCharArray();

			int iaaa = 0;
			int ibbb = 0;

			while (iaaa < aaa.length && ibbb < bbb.length)
			{
				final char ca = aaa[iaaa];
				final char cb = bbb[ibbb];
				if (charMatch(ca, cb, searchLevel))
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

		private boolean charMatch(final char a, final char b, SearchLevel searchLevel)
		{
			if (a == b)
			{
				return true;
			}
			if (searchLevel == SearchLevel.strictly)
			{
				return false;
			}

			return charMatchLoosely(a, b);
		}

		private boolean charMatchLoosely(final char a, final char b)
		{
			assert (a != b); // because of privious checking.
			final int ia = charIndexForLoosingSearch(a);
			final int ib = charIndexForLoosingSearch(b);

			if (ia < 0 || ib < 0)
			{
				return false;
			}

			int delta = ia - ib;
			return delta == 1 || delta == -1;
		}

		private int charIndexForLoosingSearch(char c)
		{
			switch (c)
			{
			case 'q':
				return 10;
			case 'w':
				return 11;
			case 'e':
				return 12;
			case 'r':
				return 13;
			case 't':
				return 14;
			case 'y':
				return 15;
			case 'u':
				return 16;
			case 'i':
				return 17;
			case 'o':
				return 18;
			case 'p':
				return 19;

			case 'a':
				return 30;
			case 's':
				return 31;
			case 'd':
				return 32;
			case 'f':
				return 33;
			case 'g':
				return 34;
			case 'h':
				return 35;
			case 'j':
				return 36;
			case 'k':
				return 37;
			case 'l':
				return 38;

			case 'z':
				return 50;
			case 'x':
				return 51;
			case 'c':
				return 52;
			case 'v':
				return 53;
			case 'b':
				return 54;
			case 'n':
				return 55;
			case 'm':
				return 56;

			default:
				return -1;
			}
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
