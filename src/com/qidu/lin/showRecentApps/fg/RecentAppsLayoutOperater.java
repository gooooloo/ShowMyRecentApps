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

package com.qidu.lin.showRecentApps.fg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

class RecentAppsLayoutOperater implements LayoutOperator
{

	private final ViewGroup parentLayout;
	private final Map<View, Pair<Boolean, Integer>> viewInfos = new HashMap<View, Pair<Boolean, Integer>>();

	RecentAppsLayoutOperater(ViewGroup parent)
	{
		this.parentLayout = parent;
	}

	@Override
	public void showView(View view)
	{
		if (!isViewShown(view))
		{
			parentLayout.addView(view, getShownViewCountAhead(view));
			putViewShown(view, true);
		}
	}

	private Boolean isViewShown(View view)
	{
		synchronized (viewInfos)
		{
			return viewInfos.containsKey(view) && viewInfos.get(view).first;
		}
	}

	private void putViewShown(View view, boolean shown)
	{
		synchronized (viewInfos)
		{
			viewInfos.put(view, new Pair<Boolean, Integer>(shown, viewInfos.get(view).second));
		}
	}

	private int getShownViewCountAhead(View view)
	{
		int cnt = 0;
		synchronized (viewInfos)
		{
			int targetViewIndex = viewInfos.get(view).second;
			for (Map.Entry<View, Pair<Boolean, Integer>> eachInfo : viewInfos.entrySet())
			{
				if (eachInfo.getValue().first && eachInfo.getValue().second < targetViewIndex)
				{
					cnt++;
				}
			}
		}
		return cnt;
	}

	@Override
	public void hideView(View view)
	{

		if (isViewShown(view))
		{
			putViewShown(view, false);
			parentLayout.removeView(view);
		}
	}

	@Override
	public void reserveViews(List<View> views)
	{
		for (View view : views)
		{
			addViewInfo(view, true);

			parentLayout.addView(view);
		}
	}

	private void addViewInfo(View view, boolean visible)
	{
		synchronized (viewInfos)
		{
			viewInfos.put(view, new Pair<Boolean, Integer>(visible, viewInfos.size()));
		}
	}

	@Override
	public View getViewByIndex(int index)
	{
		if (index < 0 || index >= viewInfos.size())
		{
			return null;
		}

		return parentLayout.getChildAt(index);
	}

	@Override
	public int getViewCount()
	{
		return parentLayout.getChildCount();
	}

}