package com.teman.plurkdog;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * plurk dog setting
 * @author chehao hsiao
 * 
 */
public class DogSetting {
	private static DogSetting me = null;
	private Properties prop = null;
	private String defaultSetting = "./dog.properties";
	
	public static DogSetting getInstance(){
		if(me == null){
			synchronized (DogSetting.class) {
				if(me ==null){
					me = new DogSetting();
				}
			}
		}
		return me;
	}
	
	private DogSetting(){
		   FileInputStream fin = null;
	        try {
	        	prop = new Properties();	
	        	ClassLoader cl = Thread.currentThread().getContextClassLoader();		
	            fin = new FileInputStream(new File(cl.getResource(defaultSetting).toURI()));
	            prop.load(fin);
	        } catch (Exception e) {
	        	System.out.println("Can't find dog.properties"+e.getMessage());
	        }
	}
	
	public String getProperty(String key){
		return prop.getProperty(key);
	}
	
	public int getInt(String key){
		return Integer.parseInt(prop.getProperty(key));
	}
}
