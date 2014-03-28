package com.qidu.lin.showRecentApps.fg;

import android.content.Context;
import android.widget.EditText;

public class AppCountTextDrawable extends TextDrawable
{
	public AppCountTextDrawable(Context context)
	{
		super(context);
		setDefaultUI(context);
	}

	private void setDefaultUI(Context context)
	{
		setTextColor(new EditText(context).getHintTextColors());
		setText("");
	}

	public void setAppCnt(int appCnt)
	{
		setText("" + appCnt);
	}

}
