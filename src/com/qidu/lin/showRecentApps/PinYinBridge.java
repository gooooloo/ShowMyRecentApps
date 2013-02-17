// Copyright @ gooooloo

package com.qidu.lin.showRecentApps;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinBridge
{

	public static Set<String> getHanyuPinyin(String name)
	{
		Set<String> hanyu = new HashSet<String>();
		for (int i = 0; i < name.length(); i++)
		{
			hanyu = Utils.product(hanyu, PinYinBridge.translate(name.charAt(i)));
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
