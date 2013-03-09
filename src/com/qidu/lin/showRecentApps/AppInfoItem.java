// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;

class AppInfoItem
{
	final private int cnt;
	final private Intent launchIntent;
	final private String packageName;

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

	public String getId(PackageManager pm)
	{
		return "" + packageName + getLabel(pm);
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

	public Drawable getIcon(PackageManager pm)
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
		return pm.getApplicationIcon(applicationInfo);
	}

	public int getCount()
	{
		return cnt;
	}

	public Intent getLaunchIntent()
	{
		return launchIntent;
	}

	public boolean equalsPackagename(String pn)
	{
		return packageName.equalsIgnoreCase(pn);
	}

	public Intent getIntentToManageApp()
	{

		return new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}
}