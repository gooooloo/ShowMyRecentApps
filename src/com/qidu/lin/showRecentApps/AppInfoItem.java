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

package com.qidu.lin.showRecentApps;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;

class AppInfoItem
{
	final private int cnt;
	final private String packageName;

	private AppInfoItem(String packageName, int cnt)
	{
		this.packageName = packageName;
		this.cnt = cnt;
	}

	public static AppInfoItem makeInstance(String packageName, int cnt)
	{
		return new AppInfoItem(packageName, cnt);
	}

	public String getId()
	{
		return "" + packageName + getLabel();
	}

	public CharSequence getLabel()
	{
		ApplicationInfo applicationInfo = null;
		try
		{
			applicationInfo = PackageManagerCache.getPm().getApplicationInfo(packageName, 0);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return PackageManagerCache.getPm().getApplicationLabel(applicationInfo);
	}

	public Drawable getIcon()
	{
		ApplicationInfo applicationInfo = null;
		try
		{
			applicationInfo = PackageManagerCache.getPm().getApplicationInfo(packageName, 0);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return PackageManagerCache.getPm().getApplicationIcon(applicationInfo);
	}

	public int getCount()
	{
		return cnt;
	}

	public Intent getLaunchIntent()
	{
		// calling getLaunchIntentForPackage() takes time, and we actually dont
		// need this until you really click on the item. So delay compute it on
		// demand.
		return PackageManagerCache.getPm().getLaunchIntentForPackage(packageName);
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