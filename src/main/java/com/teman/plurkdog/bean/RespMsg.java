package com.teman.plurkdog.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class RespMsg extends PlurkBase {

	public RespMsg(JSONObject plurk) {
		super(plurk);
	}
		 
	public String toString(){
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("[回應]");	
			sb.append(getPosted());
			sb.append("-");
			sb.append(getUser()!=null?getUser().getDisplayName():getUserId());
			sb.append(getQualifierTranslated());
			sb.append(":");
			sb.append(getContent());			
		} catch (JSONException e) {
			logger.error(e.getMessage()+"-"+ this.self);
		}
		return sb.toString(); 
	}
	
}
