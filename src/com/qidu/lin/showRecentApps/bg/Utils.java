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

package com.qidu.lin.showRecentApps.bg;

import java.util.HashSet;
import java.util.Set;

public class Utils
{

	public static Set<String> product(Set<String> a, Set<String> b)
	{
		Set<String> ret = new HashSet<String>();
		if (a.isEmpty())
		{
			for (String bb : b)
			{
				ret.add(bb);
			}
		}
		else
		{
			for (String aa : a)
			{
				for (String bb : b)
				{
					ret.add(aa + bb);
				}
			}
		}
		return ret;
	}
}
