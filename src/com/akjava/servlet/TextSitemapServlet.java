package com.akjava.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.akjava.lib.common.utils.TemplateUtils;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import com.google.common.xml.XmlEscapers;

public abstract class TextSitemapServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	
	protected boolean useCache;
	protected Map<String,String> dataMap = new HashMap<String, String>();;
	
	public static volatile Cache cache;
	public static int DEFAULT_CACHE_AGE=60*60;
	protected int cache_age=DEFAULT_CACHE_AGE;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	


	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path=null;
		
		String outputText=null;
		if(useCache){
			initCache();
			path=ServletUtils.parsePath(request, "rss.xml");
			outputText=(String) cache.get(path);
		}
		
		
		
		if(outputText==null){
		
			String domain=getServletConfig().getInitParameter("domain");
			if(domain==null){
				domain="";
				}
		
		 outputText=createSitemapText(domain);
		
		
		
		
		
		
		if(useCache && cache!=null){
			cache.put(path,outputText);
		}
		
		}
		
		if(useCache){
			
			response.setHeader("Cache-Control", "public, max-age="+cache_age);
		}else{
			
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");	
			response.setHeader("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");		
		}
		response.setContentType(getMediaType());
		response.getWriter().write(outputText);
	}
	
	public abstract List<String> getUrls();

	
	protected String getMediaType(){
		return MediaType.PLAIN_TEXT_UTF_8.toString();
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
	
	
	public String createSitemapText(String domain){
		List<String> urls=getUrls();
		if(domain.isEmpty()){
		return Joiner.on("\n").join(urls);	
		}else{
		List<String> added=Lists.transform(urls, new AddDomainFunctin(domain));
		return Joiner.on("\n").join(added);
		}
	}
	public static class AddDomainFunctin implements Function<String,String>{
		private String domain;
		public AddDomainFunctin(String domain){
			this.domain=domain;
		}
		@Override
		public String apply(String input) {
			return domain+input;
		}
		
	}
	
}
