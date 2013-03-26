package com.qidu.lin.showRecentApps;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
		private DatabaseHelper dh;

		SearchAsyncTask(Context context)
		{
			this.context = context;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params)
		{
			List<Pair<AppInfoItem, Boolean>> xxx = new ArrayList<Pair<AppInfoItem, Boolean>>();

			this.dh = new DatabaseHelper(context);
			SQLiteDatabase db = dh.doOpenDbForQuery();
			for (AppInfoItem each : AppInfoManager.getInstance().getAppInfoList())
			{
				String labelString = each.getLabel().toString();
				Boolean matched = null;
				if (labelString == null)
				{
					throw new InvalidParameterException("packageName should not be null");
				}
				else if (params[0] == null || params[0].isEmpty())
				{
					matched = true;
				}
				else if (labelString.isEmpty())
				{
					matched = false;
				}
				else
				{
					matched = DatabaseHelper.select(db, labelString, params[0]);

					if (matched == null)
					{
						matched = matchRuntime(labelString, params[0]);
						dh.insert(labelString, params[0], matched);
					}
				}
				
				xxx.add(new Pair<AppInfoItem, Boolean>(each, matched));
			}

			this.publishProgress(xxx);
			dh.doClose(db);
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
