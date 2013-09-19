package com.akjava.servlet;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;

import com.akjava.lib.common.utils.TemplateUtils;
import com.google.common.io.CharStreams;

public class SimpleTemplateUtils {
	private SimpleTemplateUtils(){}
	
	public static String createTextFromText(String template,Map<String,String> mapData){
		return TemplateUtils.createAdvancedText(template, mapData);
	}
	
	public static String createTextFromFile(ServletContext context,String filePath,Map<String,String> mapData){
		
			String template=loadTextFromFile(context, filePath);
			if(template==null){
				return "Template file not found:"+filePath;
			}
			return TemplateUtils.createAdvancedText(template, mapData);
		
	}
	public static String loadTextFromFile(ServletContext context,String filePath){
		try {
			return CharStreams.toString(new FileReader(context.getRealPath("WEB-INF/template/"+filePath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
