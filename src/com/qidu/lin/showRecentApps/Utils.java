//Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

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
