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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridView;

import com.qidu.lin.showRecentApps.R;
import com.qidu.lin.showRecentApps.bg.PackageManagerCache;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoList;
import com.qidu.lin.showRecentApps.bg.appInfo.AppInfoManager;
import com.qidu.lin.showRecentApps.bg.search.SearchManager;
import com.qidu.lin.showRecentApps.fg.RecentAppsAdapter.Decorater;

public class RecentAppsActivity extends Activity
{
	private RecentAppsAdapter adapter = null;
	private RecentAppsKeyboardAndTouchHelper keyboradAndTouchHelper = new RecentAppsKeyboardAndTouchHelper(this);
	private Handler searchHandler = new Handler(Looper.getMainLooper());
	private Runnable searchRunnable = null;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		keyboradAndTouchHelper.helpDispatchTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	void finishWithIntent(Intent intent)
	{
		startActivity(EMPTYActivity.getIntentToStart(this, intent));
		finish();
	}

	private TextWatcher getSearchTextWatcher()
	{
		return new TextWatcher()
		{
			@Override
			public void afterTextChanged(final Editable s)
			{
				// TODO: can this be simplified as
				// "SearchManager.getInstance().onSearch(s.toString());"?
				if (searchRunnable != null)
				{
					searchHandler.removeCallbacks(searchRunnable);
					searchRunnable = null;
				}
				final String searchkey = s.toString();
				searchRunnable = new Runnable()
				{
					@Override
					public void run()
					{
						SearchManager.getInstance().onSearch(searchkey);
						searchRunnable = null;
					}
				};
				searchRunnable.run();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// empty
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// empty
			}
		};
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_test_get_recent_apps);

		keyboradAndTouchHelper.setupKeyboardListener();

		adapter = new RecentAppsAdapter(this);
		((GridView) findViewById(R.id.gridView1)).setAdapter(adapter);
		adapter.setDecorater(new RecentAppsAdapter.Decorater()
		{
			@Override
			public void decorate(AppInfoList appInfoList)
			{
				((EditText) RecentAppsActivity.this.findViewById(R.id.appCnt)).setHint("" + appInfoList.size());
			}
		});

		((EditText) findViewById(R.id.searchView1)).addTextChangedListener(getSearchTextWatcher());

		PackageManagerCache.setPm(getPackageManager());

		AppInfoManager.getInstance().addListener(adapter);
		SearchManager.getInstance().setSearchResultListener(adapter);
		AppInfoManager.getInstance().refreshAsynchronized(this);
	}

	@Override
	protected void onDestroy()
	{
		if (searchRunnable != null)
		{
			searchHandler.removeCallbacks(searchRunnable);
			searchRunnable = null;
		}
		SearchManager.getInstance().setSearchResultListener(null);
		AppInfoManager.getInstance().deleteListener(adapter);
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		finishWithIntent(null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (keyboradAndTouchHelper.isEventPositionInsideRootLayout(event))
		{
			return super.onTouchEvent(event);
		}
		else
		{
			keyboradAndTouchHelper.showKeyboard();
			return true;
		}
	}

}
