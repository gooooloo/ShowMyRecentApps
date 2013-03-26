package com.qidu.lin.showRecentApps;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class SearchResultCache
{
	private Map<String, Map<String, Boolean>> resultCache = new HashMap<String, Map<String, Boolean>>();

	public static class SearchTarget
	{
		private final String label;
		private final String keyword;

		public SearchTarget(String label, String keyword)
		{
			super();
			this.label = label;
			this.keyword = keyword;
		}

		public String getLabel()
		{
			return label;
		}

		public String getKeyword()
		{
			return keyword;
		}

	}

	public boolean hasCacheFor(SearchTarget searchTarget)
	{
		if (searchTarget == null || searchTarget.getKeyword() == null || searchTarget.getLabel() == null)
		{
			return false;
		}

		return (resultCache.containsKey(searchTarget.getKeyword()))
				&& (resultCache.get(searchTarget.getKeyword()).containsKey(searchTarget.getLabel()));
	}

	public boolean getCachedResultFor(SearchTarget searchTarget)
	{
		if (searchTarget == null || searchTarget.getKeyword() == null || searchTarget.getLabel() == null)
		{
			throw new InvalidParameterException("search target is invalid");
		}

		return resultCache.get(searchTarget.getKeyword()).get(searchTarget.getLabel());
	}

	public void setCacheFor(SearchTarget searchTarget, boolean result)
	{

		if (searchTarget == null || searchTarget.getKeyword() == null || searchTarget.getLabel() == null)
		{
			return;
		}

		if (!resultCache.containsKey(searchTarget.getKeyword()))
		{
			resultCache.put(searchTarget.getKeyword(), new HashMap<String, Boolean>());
		}

		resultCache.get(searchTarget.getKeyword()).put(searchTarget.getLabel(), result);
	}
}
