// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.ArrayList;

public class AppInfoList extends ArrayList<AppInfoItem>
{
	private static final long serialVersionUID = 3917535715580059359L;

	public boolean containsThisPackage(String packageName)
	{
		for (AppInfoItem item : this)
		{
			if (item.equalsPackagename(packageName))
			{
				return true;
			}
		}
		return false;
	}
}
