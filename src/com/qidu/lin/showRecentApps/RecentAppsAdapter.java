package com.qidu.lin.showRecentApps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gridlayout.GridLayout;

public class RecentAppsAdapter implements AppInfoRefreshListener, SearchResultListener
{
	private int entry_width;
	private int entry_height;
	private int entry_gravity;
	private int entry_image_width;
	private int entry_image_height;
	private int entry_image_margin_left;
	private int entry_image_margin_right;
	private int entry_image_margin_top;
	private int entry_label_width;
	private int entry_label_height;
	private int entry_label_gravity;
	private int entry_label_max_lines;
	private float entry_label_text_size;
	private int entry_count_width;
	private int entry_count_height;
	private int entry_count_gravity;
	private int entry_count_max_lines;
	private float entry_count_text_size;
	private int entry_margin_bottom;
	private Drawable drawable;

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

		getUISettings(showGetRecentAppsActivity);
	}

	private void getUISettings(Context context)
	{
		Resources resources = context.getResources();
		this.entry_width = ViewGroup.LayoutParams.WRAP_CONTENT;
		this.entry_height = ViewGroup.LayoutParams.WRAP_CONTENT;
		this.entry_gravity = Gravity.CENTER;
		this.entry_image_width = resources.getDimensionPixelSize(R.dimen.entry_image_width);
		this.entry_image_height = resources.getDimensionPixelSize(R.dimen.entry_image_height);
		this.entry_image_margin_left = resources.getDimensionPixelSize(R.dimen.entry_image_margin_left);
		this.entry_image_margin_right = resources.getDimensionPixelSize(R.dimen.entry_image_margin_right);
		this.entry_image_margin_top = resources.getDimensionPixelSize(R.dimen.entry_image_margin_top);
		this.entry_label_width = resources.getDimensionPixelSize(R.dimen.entry_label_width);
		this.entry_label_height = ViewGroup.LayoutParams.WRAP_CONTENT;
		this.entry_label_gravity = Gravity.CENTER;
		this.entry_label_max_lines = 2;
		this.entry_label_text_size = resources.getDimension(R.dimen.entry_label_text_size);
		this.entry_count_width = resources.getDimensionPixelSize(R.dimen.entry_count_width);
		this.entry_count_height = ViewGroup.LayoutParams.WRAP_CONTENT;
		this.entry_count_gravity = Gravity.CENTER;
		this.entry_count_max_lines = 1;
		this.entry_count_text_size = resources.getDimension(R.dimen.entry_count_text_size);
		this.entry_margin_bottom = resources.getDimensionPixelSize(R.dimen.entry_margin_bottom);
		this.drawable = resources.getDrawable(android.R.drawable.stat_notify_sync);
	}

	public void refreshWithData(final AppInfoList result)
	{
		final int typeSetText = 0;
		final int typeSetImage = 1;
		final int typeReserveViews = 3;

		AsyncTask<Void, Object, Void> x = new AsyncTask<Void, Object, Void>()
		{

			private View inflateEntry(LayoutInflater fi)
			{
				// inflate from xml takes too long time, so inflate from Java
				// codes.

				Context context = fi.getContext();

				LinearLayout layout = new LinearLayout(context);
				GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
				lp.width = entry_width;
				lp.height = entry_height;
				lp.setGravity(entry_gravity);
				layout.setLayoutParams(lp);
				layout.setGravity(entry_gravity);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setBackgroundResource(R.drawable.touchfeedback);

				ImageView iv = new ImageView(context);
				iv.setId(R.id.imageView1);
				LinearLayout.LayoutParams ivlp = new LinearLayout.LayoutParams(entry_image_width, entry_image_height);
				ivlp.gravity = entry_gravity;
				ivlp.leftMargin = entry_image_margin_left;
				ivlp.rightMargin = entry_image_margin_right;
				ivlp.topMargin = entry_image_margin_top;
				iv.setLayoutParams(ivlp);
				iv.setScaleType(ScaleType.FIT_XY);
				iv.setImageDrawable(drawable);
				iv.setImageResource(android.R.drawable.stat_notify_sync);
				layout.addView(iv);

				TextView labelTv = new TextView(context);
				labelTv.setId(R.id.editText1);
				LinearLayout.LayoutParams labellp = new LinearLayout.LayoutParams(entry_label_width, entry_label_height);
				labellp.gravity = entry_label_gravity;
				labelTv.setLayoutParams(labellp);
				labelTv.setMaxLines(entry_label_max_lines);
				labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, entry_label_text_size);
				labelTv.setGravity(entry_label_gravity);
				layout.addView(labelTv);

				TextView countTV = new TextView(context);
				countTV.setId(R.id.editText2);
				LinearLayout.LayoutParams countlp = new LinearLayout.LayoutParams(entry_count_width, entry_count_height);
				labellp.gravity = entry_count_gravity;
				labellp.bottomMargin = entry_margin_bottom;
				countTV.setLayoutParams(countlp);
				countTV.setMaxLines(entry_count_max_lines);
				countTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, entry_count_text_size);
				countTV.setGravity(entry_count_gravity);
				layout.addView(countTV);

				return layout;
			}

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

			@Override
			protected Void doInBackground(Void... params)
			{
				final int maxUIReserveNum = 15;
				showResults(0, maxUIReserveNum);
				showResults(maxUIReserveNum, result.size());

				for (int i = 0; i < result.size(); i++)
				{
					PinYinBridge.getHanyuPinyin(result.get(i).getLabel().toString());
				}

				return null;
			}

			private void showResults(int startIndexInclude, int endIndexExclude)
			{

				ArrayList<View> reservedViewsUI = new ArrayList<View>();
				for (int i = startIndexInclude; i < result.size() && i < endIndexExclude; i++)
				{
					View view = inflateEntry(showGetRecentAppsActivity.getLayoutInflater());
					reservedViewsUI.add(view);
				}

				publishProgress(typeReserveViews, reservedViewsUI);
				for (int i = startIndexInclude; i < result.size() && i < endIndexExclude; i++)
				{
					final AppInfoItem xxx = result.get(i);
					final View view = reservedViewsUI.get(i - startIndexInclude);
					setupEntryViewDetails(xxx, view);
				}
			}

			private void setupEntryViewDetails(final AppInfoItem xxx, final View view)
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

				appinfoidViewMap.put(xxx.getId(), view);
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
		} else
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
		} else
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