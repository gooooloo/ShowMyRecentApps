package com.qidu.lin.showRecentApps;

import java.util.Map;

import android.os.AsyncTask;
import android.util.Pair;
import android.view.View;

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

	class SearchAsyncTask extends AsyncTask<String, Pair<String, Boolean>, Void>
	{
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params)
		{
			for (CharSequence label : AppInfoManager.getInstance().getLabels())
			{
				String labelString = label.toString();
				boolean matched = match(labelString, params[0]);
				this.publishProgress(new Pair<String, Boolean>(labelString, matched));
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Pair<String, Boolean>... values)
		{
			if (resultListner == null)
			{
				return;
			}
			for (Pair<String, Boolean> each : values)
			{
				resultListner.onSearchResult(each.first, each.second);
				// if (each.second)
				// {
				// layoutOperator.showView(each.first);
				// }
				// else
				// {
				// layoutOperator.hideView(each.first);
				// }
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
