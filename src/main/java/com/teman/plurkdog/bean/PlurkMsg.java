package com.teman.plurkdog.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import sun.util.resources.TimeZoneNames_zh_TW;

import com.google.jplurk.DateTime;

public class PlurkMsg extends PlurkBase {
	 
 
	public static final String FAVORITE = "favorite";
	public static final String LIMITED_TO = "limited_to";
	public static final String PLURK_TYPE = "plurk_type";
	public static final String RESPONSE_COUNT = "response_count";
	public static final String IS_UNREAD = "is_unread";
	public static final String RESPONSES_SEEN = "responses_seen";
	public static final String NO_COMMENTS = "no_comments";
	public static final String OWNER_ID = "owner_id";
	


	public PlurkMsg(JSONObject plurk) throws JSONException {
		super(plurk);	 
	}
	
	public String getPlurkId() throws JSONException {
		return  self.getString(PLURK_ID);
	}

	public String getOwnerId() throws JSONException {
		return self.getString(OWNER_ID);
	}
	
	public String getFavorite() throws JSONException {
		return self.getString(FAVORITE);
	}

	 
	public String getLimitedTo() throws JSONException {
		return self.getString(LIMITED_TO);
	}

	 

	public String getPlurkType() throws JSONException {
		return self.getString(PLURK_TYPE);
	}

	 

	public int getResponseCount() throws JSONException {
		return self.getInt(RESPONSE_COUNT);
	}

	 

	/**
	 *  是否已經讀
	 * @return
	 * @throws JSONException
	 */
	public boolean getIsUnread() throws JSONException {
		return self.getInt(IS_UNREAD) == 1;
	}

 
	/**
	 * 是否回應可以看見
	 * @return
	 * @throws JSONException
	 */
	public int getResponsesSeen() throws JSONException {
		return self.getInt(RESPONSES_SEEN);
	}

	 

	/**
	 * 此噗無法回應
	 * @return
	 * @throws JSONException
	 */
	public boolean getNoComments() throws JSONException {
		return self.getInt(NO_COMMENTS) == 1;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			
			
			//sb.append(getPlurkId());		
			sb.append(getUser()!=null?getUser().getDisplayName():getUserId());
			sb.append(" ");
			sb.append(getQualifierTranslated());
			sb.append(":");
			sb.append(getContent());
			sb.append(",response Count:"+getResponseCount());
			sb.append(",IsUnread:"+getIsUnread());
			sb.append(",respon_seen:"+getResponsesSeen());
			/*sb.append("\r\n 回應(");
			
			sb.append(")");
			
			sb.append(",noComments:"+getNoComments());
			
			sb.append("-");
			sb.append(sdf.format(getPosted()));*/
		} catch (JSONException e) {
			logger.error(e.getMessage()+"-"+ this.self);
		}
		return sb.toString(); 
		//return this.self.toString(); 
	}
	
	
}
