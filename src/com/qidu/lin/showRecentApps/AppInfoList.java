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
