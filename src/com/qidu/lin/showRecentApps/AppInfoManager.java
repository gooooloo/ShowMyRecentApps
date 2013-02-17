// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Intent;
import android.os.AsyncTask;

class AppInfoManager
{
	private static AppInfoManager instance;

	public static AppInfoManager getInstance(Activity activity)
	{
		if (instance == null)
		{
			instance = new AppInfoManager(activity);
		}

		return instance;
	}

	final private Activity activity;

	final private Set<AppInfoRefreshListener> listeners = new HashSet<AppInfoRefreshListener>();

	private AppInfoManager(Activity activity)
	{
		this.activity = activity;

	}

	public void addListener(AppInfoRefreshListener testGetRecentApps)
	{
		listeners.add(testGetRecentApps);
	}

	public void deleteListener(AppInfoRefreshListener testGetRecentApps)
	{
		listeners.remove(testGetRecentApps);
	}

	public void refreshAsynchronized()
	{
		new AsyncTask<Void, Void, AppInfoList>()
		{
			List<RecentTaskInfo> recentTaskInfo;

			@Override
			protected void onPreExecute()
			{
				recentTaskInfo = ((ActivityManager) activity.getSystemService(Activity.ACTIVITY_SERVICE)).getRecentTasks(100,
						ActivityManager.RECENT_WITH_EXCLUDED);
			}

			@Override
			protected AppInfoList doInBackground(Void... arg0)
			{
				AppStatisticsManager spMgr = AppStatisticsManager.getInstance(activity);

				for (RecentTaskInfo each : recentTaskInfo)
				{
					Intent intent = each.baseIntent;
					if (intent == null)
					{
						continue;
					}

					String className = intent.getComponent().getClassName();

					if (className.equalsIgnoreCase(EMPTYActivity.class.getName()))
					{
						break;
					}

					String packageName = intent.getComponent().getPackageName();

					if (packageName == null || packageName.equalsIgnoreCase(AppInfoManager.this.activity.getPackageName()))
					{
						continue;
					}

					spMgr.AddAppUsdCountByOne(packageName);
				}

				AppInfoList aaa = new AppInfoList();
				for (Map.Entry<String, Integer> eachEntry : spMgr.getAll().entrySet())
				{
					String packageName = eachEntry.getKey();
					Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);

					if (launchIntent == null)
					{
						continue;
					}

					Integer count = (Integer) eachEntry.getValue();

					aaa.add(AppInfoItem.makeInstance(packageName, count, launchIntent));
				}

				Comparator<AppInfoItem> comparator = new Comparator<AppInfoItem>()
				{

					@Override
					public int compare(AppInfoItem lhs, AppInfoItem rhs)
					{
						return rhs.getCount() - lhs.getCount();
					}
				};
				Collections.sort(aaa, comparator);

				return aaa;
			}

			@Override
			protected void onPostExecute(AppInfoList result)
			{
				for (AppInfoRefreshListener each : listeners)
				{
					each.onAppInfoRefreshed(result);
				}
			}

		}.execute();
	}

}