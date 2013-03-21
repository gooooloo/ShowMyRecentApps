package com.qidu.lin.showRecentApps;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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

	class SearchAsyncTask extends AsyncTask<String, List<Pair<AppInfoItem, Boolean>>, Void>
	{
		final Context context;

		SearchAsyncTask(Context context)
		{
			this.context = context;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params)
		{
			List<Pair<AppInfoItem, Boolean>> xxx = new ArrayList<Pair<AppInfoItem, Boolean>>();
			for (AppInfoItem each : AppInfoManager.getInstance().getAppInfoList())
			{
				String labelString = each.getLabel().toString();
				boolean matched = match(labelString, params[0]);
				xxx.add(new Pair<AppInfoItem, Boolean>(each, matched));
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
				for (Pair<AppInfoItem, Boolean> each : eachgroup)
				{
					resultListner.onSearchResult(each.first, each.second);
				}
			}
		}

		private boolean match(String packageName, String string)
		{
			if (packageName == null)
			{
				throw new InvalidParameterException("packageName should not be null");
			}

			if (string == null || string.isEmpty())
			{
				return true;
			}

			if (packageName.isEmpty())
			{
				return false;
			}

			DatabaseHelper dh = new DatabaseHelper(context);
			Boolean matched = dh.select(packageName, string);
			if (matched == null)
			{
				boolean matchedRuntime = matchRuntime(packageName, string);
				dh.insert(packageName, string, matchedRuntime);
				return matchedRuntime;
			}
			else
			{
				return matched;
			}
		}

		private boolean matchRuntime(String packageName, String string)
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

	public void onSearch(final Context context, final String string)
	{
		if (searchAsyncTask != null)
		{
			searchAsyncTask.cancel(true);
		}

		searchAsyncTask = new SearchAsyncTask(context);
		searchAsyncTask.execute(string);
	}
}
