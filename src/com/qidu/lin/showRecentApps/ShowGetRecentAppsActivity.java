// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.HashMap;
import java.util.Map;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowGetRecentAppsActivity extends Activity implements AppInfoRefreshListener
{
	private static final float esp = 100;
	private Float lastY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (findViewById(R.id.scrollView1).getScrollY() == 0)
			{
				lastY = ev.getRawY();
			}
		}
		if (ev.getAction() == MotionEvent.ACTION_UP)
		{
			if (lastY != null)
			{
				if (ev.getRawY() > lastY + esp)
				{
					showKeyboard();
				}

				lastY = null;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	public class RecentAppsAdapter
	{

		private final Map<View, String> viewLabelMap = new HashMap<View, String>();

		final LayoutOperator layoutOperator;

		public RecentAppsAdapter(LayoutOperator lo)
		{
			this.layoutOperator = lo;
		}

		public void reserveViews(int count)
		{
			for (int i = 0; i < count; i++)
			{
				layoutOperator.initView(getLayoutInflater().inflate(R.layout.entry, null));
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
						final CharSequence label = xxx.getLabel(getPackageManager());
						final Drawable icon = xxx.getIcon(getPackageManager());

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
								finishWithIntent(xxx.getLaunchIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
							}
						});
						viewLabelMap.put(view, xxx.getLabel(getPackageManager()).toString());

						publishProgress(typeShowView, view);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (isEventPositionInsideRootLayout(event))
		{
			return super.onTouchEvent(event);
		}
		else
		{
			showKeyboard();
			return true;
		}
	}

	private boolean isEventPositionInsideRootLayout(MotionEvent event)
	{
		boolean isEventPositionInsideLayout = false;

		int[] location = new int[2];
		View vv = findViewById(R.id.root);
		vv.getLocationOnScreen(location);

		float x = event.getX();
		float y = event.getY();

		int width = vv.getWidth();
		int height = vv.getHeight();
		if (0 <= x && x <= width)
		{
			if (0 <= y && y <= height)
			{
				isEventPositionInsideLayout = true;
			}
		}
		return isEventPositionInsideLayout;
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
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
			showKeyboard();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showKeyboard()
	{
		EditText editText = (EditText) findViewById(R.id.searchView1);
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// only will trigger it if no physical keyboard is open
		mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
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
