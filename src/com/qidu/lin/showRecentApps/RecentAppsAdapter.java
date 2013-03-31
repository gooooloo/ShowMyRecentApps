package com.qidu.lin.showRecentApps;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

	public void refreshWithData(final AppInfoList result)
	{
		final int typeSetText = 0;
		final int typeSetImage = 1;
		final int typeShowView = 2;
		AsyncTask<Void, Object, Void> x = new AsyncTask<Void, Object, Void>()
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
				layoutOperator.reserveViews(showGetRecentAppsActivity.getLayoutInflater(), result.size());
			}

			@Override
			protected Void doInBackground(Void... params)
			{

				for (int i = 0; i < result.size(); i++)
				{
					final AppInfoItem xxx = result.get(i);
					final View view = layoutOperator.getViewByIndex(appinfoidViewMap.size());
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

					appinfoidViewMap.put(xxx.getId(), view);

					publishProgress(typeShowView, view);
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
			return;
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