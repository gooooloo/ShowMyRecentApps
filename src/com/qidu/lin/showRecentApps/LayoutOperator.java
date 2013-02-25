// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import android.view.View;

interface LayoutOperator
{
	void initView(View view);

	void showView(View view);

	void hideView(View view);
	
	View getViewByIndex(int index);
}