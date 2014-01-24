package com.akjava.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		 ServletConfig config = getServletConfig();
		 @SuppressWarnings("unchecked")
		Enumeration<String> en=config.getInitParameterNames();
		 String mode=req.getParameter("mode");
		 if(mode!=null && "list".equals(mode)){
			
			 while(en.hasMoreElements()){
				 String from=en.nextElement();
				 String fromUrl=null;
				 if(from.startsWith("from-")){ //maybe cant start /
					 fromUrl="/"+from.substring("from-".length());
				 }
				 String to=config.getInitParameter(from);
				 String alink="<a href='"+fromUrl+"' target='link'>"+fromUrl+" - "+to+"</a>";
				 resp.getWriter().print(alink+"<br/>");
			 }
			 
			 
			 return;
		 }
		 
		 
		 while(en.hasMoreElements()){
			 String from=en.nextElement();
			 String fromUrl=null;
			 if(from.startsWith("from-")){ //maybe cant start /
				 fromUrl="/"+from.substring("from-".length());
			 }
			 //resp.getWriter().write(fromUrl);
			 if(fromUrl.equals(req.getRequestURI())){
				 String to=config.getInitParameter(from);
				 if(to==null){
					 resp.sendError(505, "not found,invalid redirect");
				 }
				 //resp.getWriter().write(to);
				 
				 
				 resp.setStatus(301);
				 resp.setHeader( "Location", to);
				 resp.setHeader( "Connection", "close" );
				return;
			 }
		 }
		 
		
			 resp.sendError(505, "not redirect registerd,invalid redirect");
		 
	}

}
