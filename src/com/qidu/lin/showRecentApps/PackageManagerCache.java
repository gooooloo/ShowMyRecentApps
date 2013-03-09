package com.qidu.lin.showRecentApps;

import android.content.pm.PackageManager;

public class PackageManagerCache
{
	static private PackageManager pm;

	public static void setPm(PackageManager pm)
	{
		PackageManagerCache.pm = pm;
	}

	public static PackageManager getPm()
	{
		return pm;
	}
}
