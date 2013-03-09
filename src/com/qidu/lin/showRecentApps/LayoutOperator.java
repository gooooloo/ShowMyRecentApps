// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import android.view.LayoutInflater;
import android.view.View;

interface LayoutOperator
{
	void showView(View view);

	void hideView(View view);
	
	View getViewByIndex(int index);

	void reserveViews(LayoutInflater fi, int count);
}