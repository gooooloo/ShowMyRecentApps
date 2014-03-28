package com.qidu.lin.showRecentApps.fg;

import android.content.Context;

public class AppCountTextDrawable extends TextDrawable
{
	public AppCountTextDrawable(Context context)
	{
		super(context);
		setDefaultUI(context);
	}

	private void setDefaultUI(Context context)
	{
		int textColorResId = android.R.color.darker_gray;
		setTextColor(context.getResources().getColor(textColorResId));
		setText("");
	}

}
