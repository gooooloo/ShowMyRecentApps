// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.List;

import android.view.View;

interface LayoutOperator
{
	void showView(View view);

	void hideView(View view);

	View getViewByIndex(int index);

	void reserveViews(List<View> views);
}