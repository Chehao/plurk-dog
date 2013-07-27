package com.teman.plurkdog.service;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.jplurk.PlurkClient;
import com.google.jplurk.PlurkSettings;
import com.google.jplurk.exception.PlurkException;
import com.teman.plurkdog.bean.PlurkUser;

/**
 * @author teman
 * 
 */
public class UserService {

	private static PlurkUser user = null;
	private static UserService me = null;
	public static Log logger = LogFactory.getLog(UserService.class);
	private PlurkClient client = null;
	private String defaultSetting = "./jplurk.properties";
	
	private UserService(String setting) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();		
		File settingCofingFile = null;
		try {
			if(!StringUtils.isEmpty(setting)){
				settingCofingFile = new File(cl.getResource(setting).toURI());
			}else{
				settingCofingFile = new File(cl.getResource(defaultSetting).toURI());
			}			
			client = new PlurkClient(new PlurkSettings(settingCofingFile));
		} catch (Exception e) {
			logger.error(e,e);
		} catch (PlurkException e) {
			logger.error(e,e);
		}		
	}

	/**
	 * 是否登入
	 * 
	 * @param user
	 * @return
	 */
	public  boolean isLogin() {
		if (user == null) {
			return false;
		} else {
			return true;
		}
	}
	

	/**
	 * 登入
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
		JSONObject rep = client.login(username, password);
		
		//logger.debug(rep);
		if (rep != null && !StringUtils.isEmpty(rep.toString())) {
			try {
				this.user = new PlurkUser(rep.getJSONObject("user_info"));
			} catch (JSONException e) {
				logger.error(e, e);
			}
			logger.debug(user);
		}
	}

	public void logout(){
		this.user = null;		
	}
	public static UserService getInstance() {
		if (me == null) {
			synchronized (UserService.class) {
				if (me == null) {
					me = new UserService(null);
				}
			}
		}
		return me;
	}
	
	public static UserService newInstance(String config){
		return new UserService(config);
	}

	public PlurkClient getClient() {
		return client;		
	}

	/**
	 * 登入使用者
	 * @return
	 */
	public PlurkUser currentUser() {
		return this.user;		
	}

}
