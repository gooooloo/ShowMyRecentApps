package com.qidu.lin.showRecentApps;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RecentAppsAdapter implements AppInfoRefreshListener
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

	private final Map<View, String> viewLabelMap = new HashMap<View, String>();

	final LayoutOperator layoutOperator;

	public RecentAppsAdapter(ShowGetRecentAppsActivity showGetRecentAppsActivity, LayoutOperator lo)
	{
		this.showGetRecentAppsActivity = showGetRecentAppsActivity;
		this.layoutOperator = lo;
	}

	public void reserveViews(int count)
	{
		for (int i = 0; i < count; i++)
		{
			layoutOperator.initView(this.showGetRecentAppsActivity.getLayoutInflater().inflate(R.layout.entry, null));
		}
	}

	public void refreshWithData(final AppInfoList result)
	{
		final int typeSetText = 0;
		final int typeSetImage = 1;
		final int typeShowView = 2;
		new AsyncTask<Void, Object, Void>()
		{

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
				case typeShowView:
					layoutOperator.showView((View) values[1]);
					break;
				default:
				}
			}

			@Override
			protected void onPreExecute()
			{
				reserveViews(result.size());
			}

			@Override
			protected Void doInBackground(Void... params)
			{

				for (int i = 0; i < result.size(); i++)
				{
					final AppInfoItem xxx = result.get(i);
					final View view = layoutOperator.getViewByIndex(viewLabelMap.size());
					final CharSequence label = xxx.getLabel(RecentAppsAdapter.this.showGetRecentAppsActivity.getPackageManager());
					final Drawable icon = xxx.getIcon(RecentAppsAdapter.this.showGetRecentAppsActivity.getPackageManager());

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
							RecentAppsAdapter.this.showGetRecentAppsActivity.finishWithIntent(xxx.getLaunchIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
						}
					});
					viewLabelMap.put(view, xxx.getLabel(RecentAppsAdapter.this.showGetRecentAppsActivity.getPackageManager()).toString());

					publishProgress(typeShowView, view);
				}

				for (int i = 0; i < result.size(); i++)
				{
					PinYinBridge.getHanyuPinyin(result.get(i).getLabel(RecentAppsAdapter.this.showGetRecentAppsActivity.getPackageManager()).toString());
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
			char[] aaa = packageName.toLowerCase().toCharArray();
			char[] bbb = string.toLowerCase().toCharArray();

			int iaaa = 0;
			int ibbb = 0;

			while (iaaa < aaa.length && ibbb < bbb.length)
			{
				if (aaa[iaaa] == bbb[ibbb])
				{
					iaaa++;
					ibbb++;
				}
				else
				{
					iaaa++;
				}
			}

			return ibbb == bbb.length;
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