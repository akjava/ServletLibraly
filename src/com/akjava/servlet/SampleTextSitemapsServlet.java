package com.akjava.servlet;

import java.util.List;

import com.google.common.collect.Lists;

public class SampleTextSitemapsServlet extends TextSitemapServlet {

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

}
