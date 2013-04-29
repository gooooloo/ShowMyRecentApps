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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

public class EMPTYActivity extends Activity
{
	private static final String NEXT_INTENT_KEY = "com.qidu.lin.showRecentApps.next.activity";

	public static Intent getIntentToStart(Context context, Intent nextActivity)
	{
		if (context == null)
		{
			return null;
		}

		Intent intent = new Intent(context, EMPTYActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (nextActivity != null)
		{
			intent.putExtra(NEXT_INTENT_KEY, nextActivity);
		}
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Parcelable extras = getIntent().getParcelableExtra(NEXT_INTENT_KEY);
		if (extras != null && extras instanceof Intent)
		{
			try
			{
				startActivity((Intent) extras);
			}
			catch (ActivityNotFoundException e)
			{
				// we dont care about this.
				// Just do nothing.
			}
		}

		finish();
	}
}
