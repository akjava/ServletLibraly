package com.akjava.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.common.net.MediaType;

public abstract class AbstractSimpleTemplateServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	protected String htmlTitle;
	protected String baseTemplateHtmlName;
	protected String mainTemplateHtmlName;
	protected boolean useCache;
	protected Map<String,String> dataMap = new HashMap<String, String>();;
	
	public static volatile Cache cache;
	public static int DEFAULT_CACHE_AGE=60*3;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path=null;
		initializeTemplate();
		String outputText=null;
		if(useCache){
			initCache();
			path=ServletUtils.parsePath(request, "index.html");
			System.out.println(path);
			outputText=(String) cache.get(path);
		}
		
		
		
		if(outputText==null){
		StringBuffer output=new StringBuffer();
		
		Map<String,String> hashMap=new HashMap<String, String>();
		
		
		String data=createMainHtml(request,response);
		if(data==null){//it must be called error ignore it
			return;
		}
		hashMap.put("data",data);
		hashMap.put("title", htmlTitle);
		
		
		String html=SimpleTemplateUtils.createTextFromFile(getServletContext(), baseTemplateHtmlName, hashMap);
		output.append(html);
		
		outputText=output.toString();
		if(useCache && cache!=null){
			cache.put(path,outputText);
		}
		
		}
		
		
		response.setHeader("Cache-Control", "public, max-age="+DEFAULT_CACHE_AGE);
		response.setContentType(MediaType.HTML_UTF_8.toString());
		response.getWriter().write(outputText);
	}
	@SuppressWarnings("unchecked")
	private void initCache() {
		CacheFactory cacheFactory;
		if(cache==null){//re create cache
		try {
			@SuppressWarnings("rawtypes")
			Map props = new HashMap();
			props.put(GCacheFactory.EXPIRATION_DELTA, DEFAULT_CACHE_AGE);// 3minute
			cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(props);
		} catch (CacheException e) {
			e.printStackTrace();
		}
		}
	}

	protected abstract boolean initializeUseCache();
	protected abstract String initializeHtmlTitle();
	protected abstract String initializeBaseTemplateHtmlName();
	protected abstract String initializeMainTemplateHtmlName();
	protected  void initializeTemplate(){
		useCache=initializeUseCache();
		htmlTitle=initializeHtmlTitle();
		baseTemplateHtmlName=initializeBaseTemplateHtmlName();
		mainTemplateHtmlName=initializeMainTemplateHtmlName();
	}
	
	protected abstract String createMainHtml(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException ;

	public String escape(String value){
		String escaped=value.replace("\"", "&quot;");
		return escaped;
	}
	
}
