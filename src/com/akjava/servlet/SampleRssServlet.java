package com.akjava.servlet;

import java.util.List;

import com.google.common.collect.Lists;

public class SampleRssServlet extends RSSServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public List<RSSItem> getRSSItems() {
		List<RSSItem> items=Lists.newArrayList();
		items.add(new RSSItem("google","http://www.google.com",System.currentTimeMillis()));
		items.add(new RSSItem("Yahoo","http://www.yahoo.co.jp",System.currentTimeMillis()-1000));
		return items;
	}

	@Override
	public boolean isCacheContent() {
		return true;
	}

	@Override
	public int getCacheSecondTimeAge() {
		return DEFAULT_CACHE_AGE;
	}

}
