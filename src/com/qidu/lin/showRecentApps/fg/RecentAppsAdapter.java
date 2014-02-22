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

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qidu.lin.showRecentApps.R;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoItem;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoList;
import com.qidu.lin.showRecentApps.fgbg.AppInfoRefreshListener;
import com.qidu.lin.showRecentApps.fgbg.SearchResultListener;

public class RecentAppsAdapter extends BaseAdapter implements AppInfoRefreshListener, SearchResultListener
{
	private final static int countMax = 16;
	private static View[] viewpool = new View[countMax];
	private AppInfoList appInfoList = new AppInfoList();
	private final RecentAppsActivity recentAppsActivity;

	public RecentAppsAdapter(RecentAppsActivity recentAppsActivity)
	{
		this.recentAppsActivity = recentAppsActivity;

		for (int i = 0; i < countMax; i++)
		{
			viewpool[i] = recentAppsActivity.getLayoutInflater().inflate(R.layout.entry, null);
		}
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}

	@Override
	public int getCount()
	{
		return Math.min(countMax, appInfoList.size());
	}

	@Override
	public Object getItem(int position)
	{
		return appInfoList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = viewpool[position];

		final AppInfoItem item = (AppInfoItem) this.getItem(position);

		((ImageView) convertView.findViewById(R.id.imageView1)).setImageDrawable(item.getIcon());
		((TextView) convertView.findViewById(R.id.editText1)).setText(item.getLabel());
		((TextView) convertView.findViewById(R.id.editText2)).setText("" + item.getCount());

		convertView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				Intent launchIntent = item.getLaunchIntent();
				if (launchIntent != null)
				{
					RecentAppsAdapter.this.recentAppsActivity.finishWithIntent(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
			}
		});

		convertView.setOnLongClickListener(new OnLongClickListener()
		{

			@Override
			public boolean onLongClick(View arg0)
			{
				startManageApp(item);
				return true;
			}

		});

		return convertView;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isEmpty()
	{
		return this.appInfoList.isEmpty();
	}

	@Override
	public boolean isEnabled(int position)
	{
		return true;
	}

	@Override
	public void onAppInfoRefreshed(AppInfoList result)
	{
		if (recentAppsActivity.isFinishing())
		{
			return;
		}

		refreshWithData(result);
	}

	@Override
	public void onSearchResult(final AppInfoList matchedList)
	{
		refreshWithData(matchedList);
	}

	public void refreshWithData(final AppInfoList result)
	{
		this.appInfoList.clear();
		this.appInfoList.addAll(result);
		this.notifyDataSetChanged();
	}

	private void startManageApp(AppInfoItem xxx)
	{
		Intent intentToManageApp = xxx.getIntentToManageApp();
		if (intentToManageApp != null)
		{
			Toast.makeText(recentAppsActivity, R.string.tip_show_app_management, Toast.LENGTH_SHORT).show();
			recentAppsActivity.startActivity(intentToManageApp);
			recentAppsActivity.finish();
		}

	}
}