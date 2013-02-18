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
import android.util.Pair;
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

			final CharSequence label = xxx.getLabel(getPackageManager());
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

			return view;
		}

		public void refreshWithData(final AppInfoList result)
		{
			new AsyncTask<Void, View, Void>()
			{
				@Override
				protected void onProgressUpdate(View... values)
				{
					for (View view : values)
					{
						layoutOperator.initAndShowView(view);
					}
				}

				@Override
				protected Void doInBackground(Void... params)
				{
					for (int i = 0; i < result.size(); i++)
					{
						AppInfoItem xxx = result.get(i);
						View view = getView(xxx);
						viewLabelMap.put(view, xxx.getLabel(getPackageManager()).toString());
						this.publishProgress(view);
					}

					for (int i = 0; i < result.size(); i++)
					{
						PinYinBridge.getHanyuPinyin(result.get(i).getLabel(getPackageManager()).toString());
					}

					return null;
				}
			}.execute();

		}

		class SearchAsyncTask extends AsyncTask<String, Pair<View, Boolean>, Void>
		{
			@SuppressWarnings("unchecked")
			@Override
			protected Void doInBackground(String... params)
			{
				// we only search for the first string
				for (Map.Entry<View, String> xx : viewLabelMap.entrySet())
				{
					if (match(xx.getValue(), params[0]))
					{
						Pair<View, Boolean> pair = new Pair<View, Boolean>(xx.getKey(), true);
						this.publishProgress(pair);
					}
					else
					{
						this.publishProgress(new Pair<View, Boolean>(xx.getKey(), false));
					}
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Pair<View, Boolean>... values)
			{
				for (Pair<View, Boolean> each : values)
				{
					if (each.second)
					{
						layoutOperator.showView(each.first);
					}
					else
					{
						layoutOperator.hideView(each.first);
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

		SearchAsyncTask searchAsyncTask = null;

		public void onSearch(final String string)
		{
			if (searchAsyncTask != null)
			{
				searchAsyncTask.cancel(true);
			}

			searchAsyncTask = new SearchAsyncTask();
			searchAsyncTask.execute(string);
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

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_test_get_recent_apps);

		final ViewGroup vv = (ViewGroup) findViewById(R.id.gridView1);

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

				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB)
				{
					setLayoutAnimationForHoneycomb(vv);
				}
				
				adapter.onSearch(s.toString());
			}
		});

		AppInfoManager.getInstance(this).addListener(this);
		AppInfoManager.getInstance(this).refreshAsynchronized();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setLayoutAnimationForHoneycomb(final ViewGroup vv)
	{
		if (vv.getLayoutTransition() == null)
		{
			LayoutTransition layoutTransition = new LayoutTransition();
			vv.setLayoutTransition(layoutTransition);
			layoutTransition.setDuration(300);
		}
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
