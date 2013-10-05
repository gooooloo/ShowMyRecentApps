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
package com.qidu.lin.showRecentApps.fg;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.qidu.lin.showRecentApps.R;

public class RecentAppsKeyboardAndTouchHelper
{
	private static final float esp = 100;
	private Float lastY;
	private final RecentAppsActivity recentAppsActivity;

	public RecentAppsKeyboardAndTouchHelper(RecentAppsActivity recentAppsActivity)
	{
		this.recentAppsActivity = recentAppsActivity;
	}

	private View findViewById(int resId)
	{
		return recentAppsActivity.findViewById(resId);
	}

	private Object getSystemService(final String id)
	{
		return recentAppsActivity.getSystemService(id);
	}

	public void helpDispatchTouchEvent(MotionEvent ev)
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
					toggleKeyboard();
				}

				lastY = null;
			}
		}
	}

	public boolean isEventPositionInsideRootLayout(MotionEvent event)
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

	// see
	// http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android.
	public void setupKeyboardListener()
	{
		final View activityRootView = findViewById(R.id.root);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				Rect r = new Rect();
				// r will be populated with the coordinates of your view that
				// area still visible.
				activityRootView.getWindowVisibleDisplayFrame(r);
				int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
				if (heightDiff > 200)
				{
					// probably the keyboard is shown...
					((EditText) findViewById(R.id.searchView1)).setHint(R.string.hint_when_keyboard_is_shown);
				}
				else if (heightDiff < -200)
				{
					// probably the keyboard is hidden...
					((EditText) findViewById(R.id.searchView1)).setHint(R.string.hint_when_keyboard_is_hidden);

				}

			}
		});
	}

	public void showKeyboard()
	{
		EditText editText = (EditText) findViewById(R.id.searchView1);
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// only will trigger it if no physical keyboard is open
		mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	public void toggleKeyboard()
	{
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
