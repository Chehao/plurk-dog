package com.teman.plurkdog.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.jplurk.Gender;
import com.teman.plurkdog.service.UserService;

public class PlurkUser {

 
	public static final String AVATAR = "avatar";
	public static final String GENDER = "gender";
	public static final String KARMA = "karma";
	public static final String FULL_NAME = "full_name";
	public static final String DISPLAY_NAME = "display_name";
	public static final String ID = "id";
	public static final String NICK_NAME = "nick_name";
	public static final String DATE_OF_BIRTH = "date_of_birth";
	public static final String LOCATION = "location";
	public static final String UID = "uid";
	public static final String RECRUITED = "recruited";
	
	private JSONObject user_info = new JSONObject() ;
	public static Log logger = LogFactory.getLog(PlurkUser.class);

	public PlurkUser(){
		
	}
	public PlurkUser(JSONObject rep) throws JSONException {
	 
		this.user_info = rep;
	}

	public int getUid() throws JSONException {
		return user_info.getInt(UID);
	}

	public String getLocation() throws JSONException {
		return user_info.getString(LOCATION);
	}
	
	public Gender getGender() throws JSONException{
		if(user_info.getInt(GENDER)==1)
			return Gender.Male;
		else 
			return Gender.Female;
	}
	
	public int getAvatar() {
		try {
			return user_info.getInt(AVATAR);
		} catch (JSONException e) {
			logger.error(e.getMessage()+"-"+ this.user_info);
		}
		return 0;
	}

	public String getDateOfBirth() throws JSONException {
		return user_info.getString(DATE_OF_BIRTH);
	}

	public String getNickName() throws JSONException {
		return user_info.getString(NICK_NAME);
	}

	public int getId() throws JSONException {
		return user_info.getInt(ID);
	}

	public int getKarma() throws JSONException {
		return user_info.getInt(KARMA);
	}

	public String getDisplayName() throws JSONException {
		return user_info.getString(DISPLAY_NAME);
	}

	public String getFullName() throws JSONException {
		return user_info.getString(FULL_NAME);
	}
	
	public String getRecruited() throws JSONException{
		return user_info.getString(RECRUITED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(getId());
			sb.append("-");			
			sb.append(getNickName());
			sb.append("(");
			sb.append(getDisplayName());
			sb.append(",");
			sb.append(getAvatar()+"歲");
			sb.append(",性別:"); 
			sb.append(getGender());
			sb.append(",Karma:");			
			sb.append(getKarma());
			sb.append(")");
			//logger.debug(user_info);
		} catch (JSONException e) {
			logger.error(e.getMessage()+"-"+ this.user_info);
		}
		return sb.toString(); 
		//return user_info.toString();
	}
}
