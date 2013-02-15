package com.qidu.lin.showRecentApps;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowGetRecentAppsActivity extends Activity implements AppInfoRefreshListener
{
	public class AppsAdapter extends BaseAdapter
	{
		private AppInfoList aaa = new AppInfoList();

		public final int getCount()
		{
			return aaa.size();
		}

		public final Object getItem(int position)
		{
			return aaa.get(position);
		}

		public final long getItemId(int position)
		{
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			if (view == null)
			{
				view = getLayoutInflater().inflate(R.layout.entry, null);
			}

			AppInfoItem xxx = aaa.get(position);

			CharSequence label = null;
			Drawable icon = null;
			try
			{
				ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(xxx.packageName, 0);

				label = getPackageManager().getApplicationLabel(applicationInfo);
				icon = getPackageManager().getApplicationIcon(applicationInfo);
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}

			if (icon != null)
			{
				((ImageView) view.findViewById(R.id.imageView1)).setImageDrawable(icon);
			}

			if (label != null)
			{
				((TextView) view.findViewById(R.id.editText1)).setText(label);
			}

			((TextView) view.findViewById(R.id.editText2)).setText("" + xxx.cnt);
			return view;
		}

		public void refreshWithData(AppInfoList result)
		{
			aaa.clear();
			aaa.addAll(result);
			notifyDataSetChanged();
		}
	}

	private AppsAdapter adapter = new AppsAdapter();

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

		final GridView vv = (GridView) findViewById(R.id.gridView1);

		vv.setAdapter(adapter);

		vv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				AppInfoItem intent = (AppInfoItem) vv.getAdapter().getItem(arg2);

				if (intent.launchIntent != null)
				{
					finishWithIntent(intent.launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
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
