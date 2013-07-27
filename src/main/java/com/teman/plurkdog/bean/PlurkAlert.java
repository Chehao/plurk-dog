package com.teman.plurkdog.bean;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

public class PlurkAlert extends PlurkBase {

	
	public static final String FROM_USER = "from_user";
	public static final String TO_USER = "to_user";
	public static final String NEW_FAN = "new_fan";
	public static final String FRIENDSHIP_PENDING = "friendship_pending";
	public static final String FRIENDSHIP_REQUEST  = "friendship_request";
		 
	//type
	//new_fan friendship_request  friendship_pending
	
	public PlurkAlert(JSONObject plurk) {
		super(plurk);		
	}
	 
	/**
	 * 從別的使用者
	 */
	private PlurkUser user = null;
	 
	 
	 
	public PlurkUser getAlertUser() throws JSONException {
		if(user == null){
			if(getType().equals(FRIENDSHIP_REQUEST)){
				user = new PlurkUser(self.getJSONObject(FROM_USER));
			}else if(getType().equals(NEW_FAN)){
				user = new PlurkUser(self.getJSONObject(NEW_FAN));
			}else if(getType().equals(FRIENDSHIP_PENDING)){
				user = new PlurkUser(self.getJSONObject(TO_USER));
			}
		}
		return user;
	}

	public void setUser(PlurkUser user) {
		this.user = user;
	}

	private static final String TYPE = "type"; 
 
	public String getType() throws JSONException{
		return self.getString(TYPE);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(getType());
			sb.append(":");				
			sb.append(getAlertUser());
			
		} catch (JSONException e) {
			logger.error(e.getMessage()+"-"+ this.self);
		}
		return sb.toString(); 
	 
	}
	
}
