// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import android.content.Intent;

class AppInfoItem
{
	final int cnt;
	final Intent launchIntent;
	final String packageName;

	public AppInfoItem(String packageName, int cnt, Intent launchIntent)
	{
		this.packageName = packageName;
		this.cnt = cnt;
		this.launchIntent = launchIntent;
	}

}