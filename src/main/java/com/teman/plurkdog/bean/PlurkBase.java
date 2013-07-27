package com.teman.plurkdog.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.jplurk.DateTime;
import com.google.jplurk.Lang;

public class PlurkBase {
	public static final String QUALIFIER_TRANSLATED = "qualifier_translated";
	public static final String QUALIFIER = "qualifier";
	public static final String POSTED = "posted";
	public static final String LANG = "lang";
	public static final String USER_ID = "user_id";
	public static final String PLURK_ID = "plurk_id";
	public static final String CONTENT = "content";
	
	
	
	protected JSONObject self = null;	
	public static Log logger = LogFactory.getLog(PlurkBase.class);
	
	public PlurkBase(JSONObject plurk) {
		this.self = plurk; 
	}

	public String toString(){
		if(self !=null){
			return self.toString();
		}else{
			return "";
		}
	}

	/**
	 * 噗或回應者
	 */
	private PlurkUser user = null;
	
	public PlurkUser getUser() {
		return user;
	}
	public void setUser(PlurkUser user) {
		this.user = user;
	} 
	
	public String getQualifierTranslated() {
		try {
			return self.getString(QUALIFIER_TRANSLATED);
		} catch (JSONException e) {
			logger.error(e.getMessage() + self);			
		}		 
		return "";
	}

	public String getQualifier() throws JSONException {
		return self.getString(QUALIFIER);
	}

	public Date getPosted() throws JSONException {
		Calendar cal = DateTime.create(self.getString(POSTED)).toCalendar();		
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return cal.getTime();
	}

	public Lang getLang() throws JSONException {		
		return Lang.valueOf(self.getString(LANG));
	}

	public String getContent() throws JSONException {
		return self.getString(CONTENT);
	}

	public String getUserId() throws JSONException {
		return self.getString(USER_ID);
	}

	
}
