package com.qidu.lin.showRecentApps;

import android.os.AsyncTask;
import android.util.Pair;

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

	class SearchAsyncTask extends AsyncTask<String, Pair<AppInfoItem, Boolean>, Void>
	{
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params)
		{
			for (AppInfoItem each : AppInfoManager.getInstance().getAppInfoList())
			{
				String labelString = each.getLabel().toString();
				boolean matched = match(labelString, params[0]);
				this.publishProgress(new Pair<AppInfoItem, Boolean>(each, matched));
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Pair<AppInfoItem, Boolean>... values)
		{
			if (resultListner == null)
			{
				return;
			}
			for (Pair<AppInfoItem, Boolean> each : values)
			{
				resultListner.onSearchResult(each.first, each.second);
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
