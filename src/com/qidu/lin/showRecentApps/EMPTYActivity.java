// Copyright @ gooooloo

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
