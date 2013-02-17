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

public class PinYinBridge
{

	private static Map<String, Set<String>> translateTable = new HashMap<String, Set<String>>();
	private static Object mutex = new Object();

	public static Set<String> getHanyuPinyin(String name)
	{
		synchronized (mutex)
		{
			if (translateTable.containsKey(name))
			{
				return translateTable.get(name);
			}
		}

		Set<String> hanyu = new HashSet<String>();
		for (int i = 0; i < name.length(); i++)
		{
			hanyu = Utils.product(hanyu, PinYinBridge.translate(name.charAt(i)));
		}

		synchronized (mutex)
		{
			if (!translateTable.containsKey(name))
			{
				translateTable.put(name, hanyu);
			}
		}
		return hanyu;
	}

	private static Set<String> translate(char ch)
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
}
