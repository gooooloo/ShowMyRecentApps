package com.qidu.lin.showRecentApps;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.qidu.lin.showRecentApps.SearchResultCache.SearchTarget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Pair;

public class SearchManager
{
	private SearchResultCache searchResultCache = new SearchResultCache();
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
			List<Pair<AppInfoItem, Boolean>> searchResults = new ArrayList<Pair<AppInfoItem, Boolean>>();
			List<DatabaseHelper.Row> writingBackResult = new ArrayList<DatabaseHelper.Row>();

			DatabaseHelper dh = new DatabaseHelper(context);
			SQLiteDatabase dbReadable = dh.doOpenDbForQuery();
			for (AppInfoItem each : AppInfoManager.getInstance().getAppInfoList())
			{
				String labelString = each.getLabel().toString();
				Boolean matched = null;
				if (labelString == null)
				{
					throw new InvalidParameterException("packageName should not be null");
				}

				final String keyword = params[0];
				if (keyword == null || keyword.isEmpty())
				{
					matched = true;
				}
				else if (labelString.isEmpty())
				{
					matched = false;
				}
				else
				{
					SearchTarget searchTarget = new SearchTarget(labelString, keyword);
					if (searchResultCache.hasCacheFor(searchTarget))
					{
						matched = searchResultCache.getCachedResultFor(searchTarget);
					}
					else
					{
						
						matched = DatabaseHelper.select(dbReadable, labelString, keyword);

						if (matched == null)
						{
							matched = matchRuntime(labelString, keyword);
							DatabaseHelper.Row row = new DatabaseHelper.Row(labelString, keyword, matched);
							writingBackResult.add(row);
						}
						
						searchResultCache.setCacheFor(searchTarget, matched);
					}
				}

				searchResults.add(new Pair<AppInfoItem, Boolean>(each, matched));
			}

			this.publishProgress(searchResults);
			dh.doClose(dbReadable);

			SQLiteDatabase dbWritable = dh.doOpenDbForUpdate();
			for (DatabaseHelper.Row row : writingBackResult)
			{
				dh.insert(dbWritable, row);
			}
			dh.doClose(dbWritable);

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
