// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.HashMap;
import java.util.Map;

import com.gridlayout.GridLayout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

class RecentAppsLayoutOperater implements LayoutOperator
{

	private final ViewGroup parentLayout;
	private final Map<View, Pair<Boolean, Integer>> viewInfos = new HashMap<View, Pair<Boolean, Integer>>();
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

	RecentAppsLayoutOperater(ViewGroup parent)
	{
		this.parentLayout = parent;
		
		getUISettings(parent.getContext());
	}

	@Override
	public void showView(View view)
	{
		if (!isViewShown(view))
		{
			parentLayout.addView(view, getShownViewCountAhead(view));
			putViewShown(view, true);
		}
	}

	private Boolean isViewShown(View view)
	{
		return viewInfos.get(view).first;
	}

	private void putViewShown(View view, boolean shown)
	{
		viewInfos.put(view, new Pair<Boolean, Integer>(shown, viewInfos.get(view).second));
	}

	private int getShownViewCountAhead(View view)
	{
		int cnt = 0;
		int targetViewIndex = viewInfos.get(view).second;
		for (Map.Entry<View, Pair<Boolean, Integer>> eachInfo : viewInfos.entrySet())
		{
			if (eachInfo.getValue().first && eachInfo.getValue().second < targetViewIndex)
			{
				cnt++;
			}
		}
		return cnt;
	}

	@Override
	public void hideView(View view)
	{

		if (isViewShown(view))
		{
			putViewShown(view, false);
			parentLayout.removeView(view);
		}
	}

	@Override
	public void reserveViews(LayoutInflater fi, int count)
	{
		for (int i = 0; i < count; i++)
		{
			View view = inflateEntry(fi);
			viewInfos.put(view, new Pair<Boolean, Integer>(true, viewInfos.size()));

			parentLayout.addView(view);
		}
	}

	private View inflateEntry(LayoutInflater fi)
	{
		// inflate from xml takes too long time, so inflate from Java codes.

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

	@Override
	public View getViewByIndex(int index)
	{
		if (index < 0 || index >= viewInfos.size())
		{
			return null;
		}

		return parentLayout.getChildAt(index);
	}

}