package com.akjava.servlet;

import java.util.List;

import com.google.common.collect.Lists;

public class SampleTextSitemapsServlet extends TextSitemapsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * this sample use domain template and so relative path only
	 */
	@Override
	public List<String> getUrls() {
		return Lists.newArrayList("/","/test.html");
	}

	@Override
	public boolean isCacheContent() {
		return true;
	}

	@Override
	public int getCacheSecondTimeAge() {
		return 60*60*8;//each 8 hour
	}

}
