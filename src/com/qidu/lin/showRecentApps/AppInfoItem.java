// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

class AppInfoItem
{
	final int cnt;
	final Intent launchIntent;
	final String packageName;

	private AppInfoItem(String packageName, int cnt, Intent launchIntent)
	{
		this.packageName = packageName;
		this.cnt = cnt;
		this.launchIntent = launchIntent;
	}
	
	public static AppInfoItem makeInstance(String packageName, int cnt, Intent launchIntent)
	{
		return new AppInfoItem(packageName, cnt, launchIntent);
	}
	
	public CharSequence getLabel(PackageManager pm)
	{
		ApplicationInfo applicationInfo = null;
		try
		{
			applicationInfo = pm.getApplicationInfo(packageName, 0);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return pm.getApplicationLabel(applicationInfo);
	}

}