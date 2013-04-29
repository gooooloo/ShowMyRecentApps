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
			}
		}
		return ret;
	}
}
