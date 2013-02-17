// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowGetRecentAppsActivity extends Activity implements AppInfoRefreshListener
{
	interface LayoutOperator
	{
		void initAndShowView(View view);

		void showView(View view);

		void hideView(View view);
	}

	class RecentAppsLayoutOperater implements LayoutOperator
	{

		private final ViewGroup parentLayout;
		private final Map<View, Pair<Boolean, Integer>> viewInfos = new HashMap<View, Pair<Boolean, Integer>>();

		RecentAppsLayoutOperater(ViewGroup parent)
		{
			this.parentLayout = parent;
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
		public void initAndShowView(View view)
		{
			Pair<Boolean, Integer> pair = new Pair<Boolean, Integer>(false, viewInfos.size());
			viewInfos.put(view, pair);
			
			showView(view);
		}

	}

	public class AppsAdapter
	{
		private final Map<View, String> viewLabelMap = new HashMap<View, String>();

		final LayoutOperator layoutOperator;

		public AppsAdapter(LayoutOperator lo)
		{
			this.layoutOperator = lo;
		}

		public View getView(final AppInfoItem xxx)
		{
			View view = getLayoutInflater().inflate(R.layout.entry, null);

			CharSequence label = null;
			Drawable icon = null;
			try
			{
				ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(xxx.packageName, 0);

				label = getPackageManager().getApplicationLabel(applicationInfo);
				icon = getPackageManager().getApplicationIcon(applicationInfo);
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}

			if (icon != null)
			{
				((ImageView) view.findViewById(R.id.imageView1)).setImageDrawable(icon);
			}

			if (label != null)
			{
				((TextView) view.findViewById(R.id.editText1)).setText(label);
			}

			((TextView) view.findViewById(R.id.editText2)).setText("" + xxx.cnt);

			view.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View arg0)
				{
					finishWithIntent(xxx.launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
			});

			viewLabelMap.put(view, label.toString());
			return view;
		}

		public void refreshWithData(AppInfoList result)
		{

			for (int i = 0; i < result.size(); i++)
			{
				final AppInfoItem yyy = result.get(i);
				new AsyncTask<Void, Void, View>()
				{

					@Override
					protected void onPostExecute(View result)
					{
						layoutOperator.initAndShowView(result);
					}

					@Override
					protected View doInBackground(Void... params)
					{
						return getView(yyy);
					}
				}.execute();
			}
		}

		public void onSearch(String string)
		{
			for (Map.Entry<View, String> xx : viewLabelMap.entrySet())
			{
				if (match(xx.getValue(), string))
				{
					layoutOperator.showView(xx.getKey());
				}
				else
				{
					layoutOperator.hideView(xx.getKey());
				}
			}
		}

		private boolean match(String packageName, String string)
		{
			if (doSimpleMatch(packageName, string))
			{
				return true;
			}

			for (String xx : getHanyuPinyin(packageName))
			{
				if (doSimpleMatch(xx, string))
				{
					return true;
				}
			}
			return false;
		}

		public Set<String> getHanyuPinyin(String packageName)
		{
			Set<String> hanyu = new HashSet<String>();
			for (int i = 0; i < packageName.length(); i++)
			{
				hanyu = product(hanyu, translate(packageName.charAt(i)));
			}
			return hanyu;
		}

		private boolean doSimpleMatch(String packageName, String string)
		{
			return packageName.toLowerCase().contains(string.toLowerCase());
		}

		private Set<String> translate(char ch)
		{
			HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
			outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
			outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);

			String[] pinyinStrings = null;
			try
			{
				pinyinStrings = PinyinHelper.toHanyuPinyinStringArray(ch, outputFormat);
			}
			catch (BadHanyuPinyinOutputFormatCombination e)
			{
				e.printStackTrace();
			}

			Set<String> ret = new HashSet<String>();
			ret.add(String.valueOf(ch));
			if (pinyinStrings != null)
			{
				for (String pinyin : pinyinStrings)
				{
					ret.add(pinyin);

					// we also support the first charactor search for Pinyin
					ret.add(pinyin.substring(0, 1));
				}
			}
			return ret;
		}

		private Set<String> product(Set<String> a, Set<String> b)
		{
			Set<String> ret = new HashSet<String>();
			if (a.isEmpty())
			{
				for (String bb : b)
				{
					ret.add(bb);
				}
			}
			else
			{
				for (String aa : a)
				{
					for (String bb : b)
					{
						ret.add(aa + bb);
					}
				}
			}
			return ret;
		}
	}

	private AppsAdapter adapter = null;

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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_test_get_recent_apps);

		final ViewGroup vv = (ViewGroup) findViewById(R.id.gridView1);
		LayoutTransition layoutTransition = new LayoutTransition();
		vv.setLayoutTransition(layoutTransition);
		layoutTransition.setDuration(300);

		adapter = new AppsAdapter(new RecentAppsLayoutOperater(vv));

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
				adapter.onSearch(s.toString());
			}
		});

		AppInfoManager.getInstance(this).addListener(this);
		AppInfoManager.getInstance(this).refreshAsynchronized();
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
