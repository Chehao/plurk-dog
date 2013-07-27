package com.teman.plurkdog.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.jplurk.Gender;
import com.teman.plurkdog.service.UserService;

public class PlurkUserProfile  {

 
	
	private JSONObject publicProfile = new JSONObject() ;
	 
	public static Log logger = LogFactory.getLog(PlurkUserProfile.class);

	private PlurkUser user = null;
	
	public PlurkUser getUser() {
		return user;
	}
	public void setUser(PlurkUser user) {
		this.user = user;
	}
	public PlurkUserProfile(){
		
	}
	public PlurkUserProfile(JSONObject rep) throws JSONException {
		user = new PlurkUser(rep.getJSONObject("user_info"));
		this.publicProfile = rep;
	}
 
	/**
	 * 為粉絲
	 * @return
	 * @throws JSONException
	 */
	public boolean isFan() throws JSONException{
		return publicProfile.getBoolean("is_fan");
	}
	
	/**
	 * 有讀取權限
	 * @return
	 * @throws JSONException
	 */
	public boolean hasReadPermission() throws JSONException{
		return publicProfile.getBoolean("has_read_permission");
	}

	public int getFriendsCount() throws JSONException{
		return publicProfile.getInt("friends_count");
	}
	
	public int getFansCount() throws JSONException{
		return publicProfile.getInt("fans_count");
	}
	
	public boolean areFriends() throws JSONException{
		return publicProfile.getBoolean("are_friends");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(user);
			sb.append("are friend:"+areFriends()+",");
			sb.append("is fan:"+isFan()+",");
			sb.append("friends count:"+getFriendsCount()+",");
			sb.append("fans count:"+getFansCount()+",");
			//logger.debug(user_info);
		} catch (JSONException e) {
			logger.error(e.getMessage()+"-"+ this.publicProfile);
		}
		return sb.toString(); 
		//return user_info.toString();
	}
}
