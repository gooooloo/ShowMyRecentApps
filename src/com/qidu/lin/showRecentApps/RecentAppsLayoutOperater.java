// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.HashMap;
import java.util.Map;

import android.util.Pair;
import android.view.LayoutInflater;
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
		return viewInfos.get(view).first;
	}

	private void putViewShown(View view, boolean shown)
	{
		viewInfos.put(view, new Pair<Boolean, Integer>(shown, viewInfos.get(view).second));
	}

	private int getShownViewCountAhead(View view)
	{
		int cnt = 0;
		int targetViewIndex = viewInfos.get(view).second;
		for (Map.Entry<View, Pair<Boolean, Integer>> eachInfo : viewInfos.entrySet())
		{
			if (eachInfo.getValue().first && eachInfo.getValue().second < targetViewIndex)
			{
				cnt++;
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
	public void reserveViews(LayoutInflater fi, int count)
	{
		for (int i = 0; i < count; i++)
		{
			// TODO : inflate takes too long time.
			View view = fi.inflate(R.layout.entry, null);
			viewInfos.put(view, new Pair<Boolean, Integer>(true, viewInfos.size()));

			parentLayout.addView(view);
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

}