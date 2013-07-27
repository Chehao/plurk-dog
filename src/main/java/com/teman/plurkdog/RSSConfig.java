package com.teman.plurkdog;

import java.text.MessageFormat;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;



public class RSSConfig {
	
	private Configuration config = null;
	private static RSSConfig me = null;
	
	private RSSConfig(String configFile) {
		try {
			config = new XMLConfiguration(configFile);
			((XMLConfiguration)config).setExpressionEngine(new XPathExpressionEngine());

		} catch (ConfigurationException e) {	
			e.printStackTrace();
		}
	}
	
	public static RSSConfig getInstance() {
		if (me == null) {
			synchronized (RSSConfig.class) {
				if (me == null){
					me = new RSSConfig("data/userlist.xml");
				}
			}
		}
		return me;
	}
	
	public String getFamiliar(String id) {
		return getProperty(MessageFormat.format("user[@id=''{0}'']/familiar", 
				new Object[] {id }
		));
	}
	
	public void setFamiliar(String id, String value) {
		setProperty(MessageFormat.format("user[@id=''{0}'']/familiar", 
				new Object[] {id}),value);
	}
	
	public String getSubscribe(String id) {
		return getProperty(MessageFormat.format("user[@id=''{0}'']/subscribe", 
				new Object[] {id }
		));
	}
	
	public void setSubscribe(String id, String value) {
		setProperty(MessageFormat.format("user[@id=''{0}'']/subscribe", 
				new Object[] {id}),value);
	}
	
	public String getName(String id) {
		return getProperty(MessageFormat.format("user[@id=''{0}'']/@name", 
				new Object[] {id }
		));
	}
	public void setName(String id, String value) {
		setProperty(MessageFormat.format("user[@id=''{0}'']/@name", 
				new Object[] {id}),value);
	}
	
	public void addNewSubscribe(String id, String subscribe) {
		String subs = getSubscribe(id);
		if(subs == null) {
			addProperty("/ user/subscribe",  subscribe);
			addProperty("user[last()] @id",id);
			addProperty("user[last()] @name","");
			addProperty("user[last()] familiar","0");
		}
	}
	
	public String[] getUserIds() {
		
		return config.getStringArray("user/@id");
	} 
	
	public String[] getGlobalSubscribe(){
	    return config.getStringArray("globaluser/subscribe");
	}
	
	public String getProperty(String name) {		
		return config.getString(name);
	}
	
	public int getInt(String name){
		return config.getInt(name);
	}

	public void setProperty(String name,String value) {
		config.setProperty(name, value);
	}
	
	public void addProperty(String name , String value) {
		config.addProperty(name, value);
	}
	
	
	
	public void reload() {	
		((XMLConfiguration)config).reload();
		
	}
	public void save() {
		try {
			((XMLConfiguration)config).save();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		 
		/*RSSModel.getInstance().addNewSubscribe("1234566", "http://xxx.rss/");		
		RSSModel.getInstance().setFamiliar("1234566", "100");
		RSSModel.getInstance().setName("1234566", "teman"); */
		 
		/*RSSModel.getInstance().addProperty("/ user/subscribe", "http://www.google.com.tw");
		RSSModel.getInstance().addProperty("user[last()] @name", "google");
		RSSModel.getInstance().addProperty("user[last()] @id", "134242");
		RSSModel.getInstance().addProperty("user[last()] familiar", "0");*/
		//System.out.println(RSSModel.getInstance().getProperty("user[@id='12345']/@id"));
		//RSSModel.getInstance().setSubscribe("4103958", "http://api.flickr.com/services/feeds/groups_pool.gne?id=700597@N20&lang=zh-hk&format=rss_200");
		String[] ids = RSSConfig.getInstance().getUserIds();
		for(int i = 0 ; i < ids.length; i++)
			System.out.println(ids[i]);
		RSSConfig.getInstance().save();
		String globalsubscribe[] = RSSConfig.getInstance().getGlobalSubscribe();
		for(int i = 0; i < globalsubscribe.length;i++)
		    System.out.println(globalsubscribe[i]);
		
	}

	

}
