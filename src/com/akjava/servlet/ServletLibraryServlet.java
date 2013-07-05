package com.akjava.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class ServletLibraryServlet extends AbstractSimpleTemplateServlet {
	@Override
	protected String createMainHtml(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> mainMap=Maps.newHashMap();
		mainMap.put("main", "world");
		return SimpleTemplateUtils.createTextFromFile(getServletContext(), mainTemplateHtmlName, mainMap);
	}

	@Override
	protected String initializeHtmlTitle() {
		// TODO Auto-generated method stub
		return "test";
	}

	@Override
	protected String initializeBaseTemplateHtmlName() {
		// TODO Auto-generated method stub
		return "test1.txt";
	}

	@Override
	protected String initializeMainTemplateHtmlName() {
		// TODO Auto-generated method stub
		return "test2.txt";
	}

	@Override
	protected boolean initializeUseCache() {
		cache_age=60;//you can custome cache age here
		return true;
	}



}
