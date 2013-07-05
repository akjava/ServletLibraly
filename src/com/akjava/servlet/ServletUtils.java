package com.akjava.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Joiner;

public class ServletUtils {
	public static String requestToQuery(HttpServletRequest request,String encode){
		List<String> kv=new ArrayList<String>();
		
		@SuppressWarnings("rawtypes")
		Enumeration en=request.getParameterNames();
		while(en.hasMoreElements()){
			String key=(String) en.nextElement();
			
			String[] vs=request.getParameterValues(key);
			if(vs!=null){
				for(String v:vs){
					String ev=v;
					if(encode!=null){
						try {
							ev=URLEncoder.encode(ev, encode);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					kv.add(key+"="+ev);
				}
			}
		}
		
		
		return Joiner.on('&').join(kv);
	}
	
	public static String parsePath(HttpServletRequest request,String welcomeName){
		String path=request.getRequestURI();
		if(welcomeName!=null){
			if(path.endsWith(welcomeName)){
				path=path.substring(0,path.length()-welcomeName.length());
			}
		}
		if(request.getQueryString()!=null){
			path+="?"+request.getQueryString();
		}
		return path;
	}
	
	public static int parsePageNumber(HttpServletRequest request,String pageName){
		String pageParam=request.getParameter("page");
		if(pageParam==null){
			pageParam="1";
		}
		int page=1;
		try{
		page=Integer.parseInt(pageParam);
		}catch(Exception e){
			System.out.println("invalid page number:"+pageParam);
		}
		if(page<1){
			page=1;
		}
		return page;
	}
}
