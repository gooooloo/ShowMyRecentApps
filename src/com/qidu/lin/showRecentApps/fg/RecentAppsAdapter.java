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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qidu.lin.showRecentApps.R;
import com.qidu.lin.showRecentApps.bg.PinYinBridge;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoItem;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoList;
import com.qidu.lin.showRecentApps.fgbg.AppInfoRefreshListener;
import com.qidu.lin.showRecentApps.fgbg.SearchResultListener;

public class RecentAppsAdapter implements AppInfoRefreshListener, SearchResultListener
{

	@Override
	public void onAppInfoRefreshed(AppInfoList result)
	{
		if (showGetRecentAppsActivity.isFinishing())
		{
			return;
		}

		refreshWithData(result);
	}

	/**
	 * 
	 */
	private final ShowGetRecentAppsActivity showGetRecentAppsActivity;

	private final Map<String, View> appinfoidViewMap = new HashMap<String, View>();

	final LayoutOperator layoutOperator;

	public RecentAppsAdapter(ShowGetRecentAppsActivity showGetRecentAppsActivity, LayoutOperator lo)
	{
		this.showGetRecentAppsActivity = showGetRecentAppsActivity;
		this.layoutOperator = lo;
	}

	private View inflateEntry(LayoutInflater fi)
	{
		return fi.inflate(R.layout.entry, null);
	}

	abstract private class ItemViewSetupAsyncTask extends AsyncTask<Void, Object, Void>
	{

		final protected int typeSetText = 0;
		final protected int typeSetImage = 1;
		final protected int typeReserveViews = 3;

		@Override
		protected void onProgressUpdate(Object... values)
		{
			switch ((Integer) values[0])
			{
			case typeSetText:
				((TextView) values[1]).setText((CharSequence) values[2]);
				break;
			case typeSetImage:
				((ImageView) values[1]).setImageDrawable((Drawable) values[2]);
				break;
			case typeReserveViews:
				layoutOperator.reserveViews((List<View>) values[1]);
				break;
			default:
				// empty
			}
		}

		protected void setupEntryViewDetails(final AppInfoItem xxx, final View view)
		{
			final CharSequence label = xxx.getLabel();
			final Drawable icon = xxx.getIcon();

			if (icon != null)
			{
				publishProgress(typeSetImage, (ImageView) view.findViewById(R.id.imageView1), icon);
			}

			if (label != null)
			{
				publishProgress(typeSetText, (TextView) view.findViewById(R.id.editText1), label);
			}

			{
				publishProgress(typeSetText, (TextView) view.findViewById(R.id.editText2), "" + xxx.getCount());
			}

			view.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					Intent launchIntent = xxx.getLaunchIntent();
					if (launchIntent != null)
					{
						RecentAppsAdapter.this.showGetRecentAppsActivity.finishWithIntent(launchIntent
								.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					}
				}
			});

			view.setOnLongClickListener(new OnLongClickListener()
			{

				@Override
				public boolean onLongClick(View arg0)
				{
					startManageApp(xxx);
					return true;
				}

			});

		}
	}

	public void refreshWithData(final AppInfoList result)
	{

		AsyncTask<Void, Object, Void> x = new ItemViewSetupAsyncTask()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				ArrayList<View> viewList = new ArrayList<View>();
				for (int i2 = 0; i2 < result.size(); i2++)
				{
					final AppInfoItem item = result.get(i2);
					View view = appinfoidViewMap.get(item.getId());
					if (view == null)
					{
						view = inflateEntry(showGetRecentAppsActivity.getLayoutInflater());
						appinfoidViewMap.put(item.getId(), view);
					}
					viewList.add(view);
				}

				publishProgress(typeReserveViews, viewList);
				for (int i1 = 0; i1 < result.size(); i1++)
				{
					final AppInfoItem item = result.get(i1);
					final View view = viewList.get(i1);
					setupEntryViewDetails(item, view);
				}

				for (int i = 0; i < result.size(); i++)
				{
					PinYinBridge.getHanyuPinyin(result.get(i).getLabel().toString());
				}

				return null;
			}

		};

		executeRefreshWithDataAsyncTask(x);

	}

	@SuppressLint("NewApi")
	private void executeRefreshWithDataAsyncTask(AsyncTask<Void, Object, Void> x)
	{
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB)
		{
			x.executeOnExecutor(x.THREAD_POOL_EXECUTOR);
		}
		else
		{
			x.execute();
		}
	}

	@Override
	public void onSearchResult(AppInfoItem appInfoItem, Boolean matched)
	{
		View view = appinfoidViewMap.get(appInfoItem.getId());
		if (view == null)
		{
			view = inflateEntry(showGetRecentAppsActivity.getLayoutInflater());
			appinfoidViewMap.put(appInfoItem.getId(), view);
			List<View> list = new ArrayList<View>();
			list.add(view);
			layoutOperator.reserveViews(list);
		}

		if (matched)
		{
			layoutOperator.showView(view);
		}
		else
		{
			layoutOperator.hideView(view);
		}
	}

	private void startManageApp(AppInfoItem xxx)
	{
		Intent intentToManageApp = xxx.getIntentToManageApp();
		if (intentToManageApp != null)
		{
			Toast.makeText(showGetRecentAppsActivity, R.string.tip_show_app_management, Toast.LENGTH_SHORT).show();
			showGetRecentAppsActivity.startActivity(intentToManageApp);
			showGetRecentAppsActivity.finish();
		}

	}
}