// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;


import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ShowGetRecentAppsActivity extends Activity
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

	private RecentAppsAdapter adapter = null;

	void finishWithIntent(Intent intent)
	{
		startActivity(EMPTYActivity.getIntentToStart(this, intent));
		finish();

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_test_get_recent_apps);

		final ViewGroup vv = (ViewGroup) findViewById(R.id.gridView1);

		adapter = new RecentAppsAdapter(this, new RecentAppsLayoutOperater(vv));

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

				SearchManager.getInstance().onSearch(s.toString());
			}
		});

		AppInfoManager.getInstance().addListener(adapter);
		SearchManager.getInstance().setSearchResultListener(adapter);
		AppInfoManager.getInstance().refreshAsynchronized(this);
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
}
