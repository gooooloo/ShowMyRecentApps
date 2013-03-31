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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;

class AppInfoManager
{
	private static AppInfoManager instance;

	private AppInfoList packageLabels = new AppInfoList();

	private void addPackageLabel(AppInfoItem appInfo)
	{
		synchronized (packageLabels)
		{
			packageLabels.add(appInfo);
		}
	}

	public AppInfoList getAppInfoList()
	{
		synchronized (packageLabels)
		{
			return packageLabels;
		}
	}

	public static AppInfoManager getInstance()
	{
		if (instance == null)
		{
			instance = new AppInfoManager();
		}

		return instance;
	}

	final private Set<AppInfoRefreshListener> listeners = new HashSet<AppInfoRefreshListener>();

	public void addListener(AppInfoRefreshListener testGetRecentApps)
	{
		listeners.add(testGetRecentApps);
	}

	public void deleteListener(AppInfoRefreshListener testGetRecentApps)
	{
		listeners.remove(testGetRecentApps);
	}

	public void refreshAsynchronized(final Activity activity)
	{
		new AsyncTask<Void, AppInfoList, Void>()
		{
			List<RecentTaskInfo> recentTaskInfo;

			@Override
			protected void onPreExecute()
			{
				recentTaskInfo = ((ActivityManager) activity.getSystemService(Activity.ACTIVITY_SERVICE)).getRecentTasks(100,
						ActivityManager.RECENT_WITH_EXCLUDED);
			}

			@Override
			protected Void doInBackground(Void... arg0)
			{
				AppStatisticsManager appStatMgr = AppStatisticsManager.getInstance(activity);

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

					if (packageName == null || packageName.equalsIgnoreCase(activity.getPackageName()))
					{
						continue;
					}

					appStatMgr.AddAppUsdCountByOne(packageName);
				}

				AppInfoList statedAppInfoList = new AppInfoList();
				PackageManager pm = PackageManagerCache.getPm();
				for (Map.Entry<String, Integer> eachEntry : appStatMgr.getAll().entrySet())
				{
					String packageName = eachEntry.getKey();

					// detect whether the package is installed. We dont care
					// about GIDS actually, but because we dont have directly
					// api to detect whether a package is installed, so we query
					// the package info, and choosing the GET_GIDS is because it
					// would be fast compared with other options.
					try
					{
						pm.getPackageInfo(packageName, PackageManager.GET_GIDS);
					}
					catch (NameNotFoundException e)
					{
						continue;
					}

					Integer count = (Integer) eachEntry.getValue();

					statedAppInfoList.add(AppInfoItem.makeInstance(packageName, count));
				}

				Comparator<AppInfoItem> comparator = new Comparator<AppInfoItem>()
				{

					@Override
					public int compare(AppInfoItem lhs, AppInfoItem rhs)
					{
						return rhs.getCount() - lhs.getCount();
					}
				};
				Collections.sort(statedAppInfoList, comparator);

				for (AppInfoItem each : statedAppInfoList)
				{
					addPackageLabel(each);
				}

				publishProgress(statedAppInfoList);

				// getInstalledPackages takes time.
				AppInfoList installedAppInfoList = new AppInfoList();
				for (PackageInfo xx : pm.getInstalledPackages(0))
				{
					String packageName = xx.packageName;
					if (statedAppInfoList.containsThisPackage(packageName))
					{
						continue;
					}

					installedAppInfoList.add(AppInfoItem.makeInstance(packageName, 0));

				}

				for (AppInfoItem each : installedAppInfoList)
				{
					addPackageLabel(each);
				}

				publishProgress(installedAppInfoList);
				return null;
			}

			@Override
			protected void onProgressUpdate(AppInfoList... values)
			{
				for (AppInfoList result : values)
				{
					for (AppInfoRefreshListener each : listeners)
					{
						each.onAppInfoRefreshed(result);
					}
				}
			}

		}.execute();
	}

}