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

public abstract class RSSServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	
	
	protected Map<String,String> dataMap = new HashMap<String, String>();;
	
	public static volatile Cache cache;
	public static int DEFAULT_CACHE_AGE=60*60;//1 hour
	//protected int cache_age=DEFAULT_CACHE_AGE;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	
	String baseTemplate="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<rss version=\"2.0\">\n"+
	    "<channel>\n"+
	        "<title>${title}</title>\n"+
	        "<link>${link}</link>\n"+
	       " <description>${description}</description>\n"
	        +"${data}\n"+
	        "</channel>\n"+
	        "</rss>";

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path=null;
		
		boolean useCache=isCacheContent();
		//System.out.println("useCache:"+useCache);
		String outputText=null;
		if(useCache){
			initCache();
			path=ServletUtils.parsePath(request, "rss.xml");
			outputText=(String) cache.get(path);
		}
		
		int cache_age=getCacheSecondTimeAge();
		
		if(outputText==null){//some case cache faild
		//	System.out.println("null output:"+path);
		StringBuffer output=new StringBuffer();
		
		Map<String,String> hashMap=new HashMap<String, String>();
		
		String data=crateRSSItemArea(request.getRequestURI());
		if(data==null){//it must be called error ignore it
			return;
		}
		
		//getInitialize parameter
		String siteTitle=getServletConfig().getInitParameter("title");
		if(siteTitle==null){
			siteTitle="website";
		}
		String description=getServletConfig().getInitParameter("description");
		if(description==null){
			description="";
		}
		String link=getServletConfig().getInitParameter("link");
		if(link==null){
			link="";
		}
		
		hashMap.put("title", XmlEscapers.xmlContentEscaper().escape(siteTitle));
		hashMap.put("description", XmlEscapers.xmlContentEscaper().escape(description));
		hashMap.put("link", XmlEscapers.xmlContentEscaper().escape(link));
		
		hashMap.put("data",data);
		
		
		
		String html=SimpleTemplateUtils.createTextFromText(baseTemplate, hashMap);
		output.append(html);
		
		outputText=output.toString();
		if(useCache && cache!=null){
			//System.out.println("put cache:"+path);
			cache.put(path,outputText);
		}else{
			//System.out.println("useCache:"+useCache+",null="+(cache==null));
		}
		
		}else{
			//System.out.println("load from cache");
		}
		
		if(useCache){
			//System.out.println("use cache:"+cache_age);
			response.setHeader("Cache-Control", "public, max-age="+cache_age);
		}else{
			
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");	
			response.setHeader("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");		
		}
		response.setContentType(getMediaType());
		response.getWriter().write(outputText);
	}
	
	public abstract List<RSSItem> getRSSItems(String url);
	public abstract boolean isCacheContent();
	public abstract int getCacheSecondTimeAge();
	
	public static class RSSItem{
		private String title;
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public RSSItem(String title, String link, long pubDate) {
			super();
			this.title = title;
			this.link = link;
			this.pubDate = pubDate;
		}
		
		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		public long getPubDate() {
			return pubDate;
		}
		public void setPubDate(long pubDate) {
			this.pubDate = pubDate;
		}
		private String link;
		private long pubDate;
	}
	
	protected String getMediaType(){
		return MediaType.XML_UTF_8.toString();
	}
	
	@SuppressWarnings("unchecked")
	private void initCache() {
		CacheFactory cacheFactory;
		if(cache==null){//re create cache
		try {
			//System.out.println("null cache initialized");
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
	
	
	public String crateRSSItemArea(String pathInfo){
		List<RSSItem> items=getRSSItems(pathInfo);
		return Joiner.on("\n").join(Lists.transform(items, new RSSItemToText()));
	}

	public static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss Z",Locale.US);
	public static class RSSItemToText implements Function<RSSItem,String>{
		
		String template="<item>"+
"<title>${title}</title>\n"+
"<link>${link}</link>\n"+
"<pubDate>${pubDate}</pubDate>\n"+
"</item>\n";

		
		@Override
		public String apply(RSSItem input) {
			
			Map<String,String> map=new HashMap<String, String>();
			map.put("pubDate", sdf.format(new Date(input.getPubDate())));
			map.put("title", XmlEscapers.xmlContentEscaper().escape(input.getTitle()!=null?input.getTitle():""));
			map.put("link", XmlEscapers.xmlContentEscaper().escape(input.getLink()!=null?input.getLink():""));
			return TemplateUtils.createText(template, map);
		}
		
	}
	

	
}
