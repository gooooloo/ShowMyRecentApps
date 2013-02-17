// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.HashMap;
import java.util.Map;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowGetRecentAppsActivity extends Activity implements AppInfoRefreshListener
{
	public class RecentAppsAdapter
	{
		private final Map<View, String> viewLabelMap = new HashMap<View, String>();

		final LayoutOperator layoutOperator;

		public RecentAppsAdapter(LayoutOperator lo)
		{
			this.layoutOperator = lo;
		}

		public View getView(final AppInfoItem xxx)
		{
			View view = getLayoutInflater().inflate(R.layout.entry, null);

			CharSequence label = xxx.getLabel(getPackageManager());
			Drawable icon = xxx.getIcon(getPackageManager());

			if (icon != null)
			{
				((ImageView) view.findViewById(R.id.imageView1)).setImageDrawable(icon);
			}

			if (label != null)
			{
				((TextView) view.findViewById(R.id.editText1)).setText(label);
			}

			((TextView) view.findViewById(R.id.editText2)).setText("" + xxx.getCount());

			view.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					finishWithIntent(xxx.getLaunchIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
			});

			viewLabelMap.put(view, label.toString());
			return view;
		}

		public void refreshWithData(AppInfoList result)
		{

			for (int i = 0; i < result.size(); i++)
			{
				final AppInfoItem yyy = result.get(i);
				new AsyncTask<Void, Void, View>()
				{

					@Override
					protected void onPostExecute(View result)
					{
						layoutOperator.initAndShowView(result);
					}

					@Override
					protected View doInBackground(Void... params)
					{
						return getView(yyy);
					}
				}.execute();
			}
		}

		public void onSearch(String string)
		{
			for (Map.Entry<View, String> xx : viewLabelMap.entrySet())
			{
				if (match(xx.getValue(), string))
				{
					layoutOperator.showView(xx.getKey());
				}
				else
				{
					layoutOperator.hideView(xx.getKey());
				}
			}
		}

		private boolean match(String packageName, String string)
		{
			if (doSimpleMatch(packageName, string))
			{
				return true;
			}

			for (String xx : PinYinBridge.getHanyuPinyin(packageName))
			{
				if (doSimpleMatch(xx, string))
				{
					return true;
				}
			}
			return false;
		}

		private boolean doSimpleMatch(String packageName, String string)
		{
			return packageName.toLowerCase().contains(string.toLowerCase());
		}
	}

	private RecentAppsAdapter adapter = null;

	private void finishWithIntent(Intent intent)
	{
		startActivity(EMPTYActivity.getIntentToStart(this, intent));
		finish();

	}

	@Override
	public void onAppInfoRefreshed(AppInfoList result)
	{
		if (isFinishing())
		{
			return;
		}

		adapter.refreshWithData(result);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_test_get_recent_apps);

		final ViewGroup vv = (ViewGroup) findViewById(R.id.gridView1);
		LayoutTransition layoutTransition = new LayoutTransition();
		vv.setLayoutTransition(layoutTransition);
		layoutTransition.setDuration(300);

		adapter = new RecentAppsAdapter(new RecentAppsLayoutOperater(vv));

		((EditText) findViewById(R.id.searchView1)).addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// empty
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// empty
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				adapter.onSearch(s.toString());
			}
		});

		AppInfoManager.getInstance(this).addListener(this);
		AppInfoManager.getInstance(this).refreshAsynchronized();
	}

	@Override
	protected void onDestroy()
	{
		AppInfoManager.getInstance(this).deleteListener(this);
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		finishWithIntent(null);
	}
}
