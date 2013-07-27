package com.teman.plurkdog;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teman.plurkdog.PlurkFilter.Predicate;
import com.teman.plurkdog.bean.PlurkAlert;
import com.teman.plurkdog.bean.PlurkBase;
import com.teman.plurkdog.bean.PlurkMsg;
import com.teman.plurkdog.bean.PlurkUser;
import com.teman.plurkdog.bean.RespMsg;

public class PlurkUtils {
	public static Log logger = LogFactory.getLog(PlurkUtils.class);

	public static List<PlurkMsg> toPlurks(JSONObject plurks) {
		List<PlurkMsg> list = new ArrayList<PlurkMsg>();
		try {
			JSONArray parray = plurks.getJSONArray("plurks");
			JSONObject plurkUsers = plurks.getJSONObject("plurk_users");
			PlurkMsg p_msg = null;
			// plurks
			for (int i = 0; i < parray.length(); i++) {
				p_msg = new PlurkMsg(parray.getJSONObject(i));
				list.add(p_msg);
			}
			// Plurk Users
			for (PlurkMsg p : list) {
				JSONObject user = plurkUsers.getJSONObject(((PlurkMsg)p).getOwnerId());
				p.setUser(new PlurkUser(user));
			}

		} catch (JSONException e) {
			logger.error(e.getMessage() + "-" + plurks);
		}

		return list;
	}

	public static List<RespMsg> toResps(JSONObject resps) {
		List<RespMsg> list = new ArrayList<RespMsg>();
		try {
			JSONObject friends = resps.getJSONObject("friends");
			JSONArray rarray = resps.getJSONArray("responses");
			RespMsg p_msg = null;
			// Responses
			for (int i = 0; i < rarray.length(); i++) {
				p_msg = new RespMsg(rarray.getJSONObject(i));
				list.add(p_msg);
			}
			// Responsed User
			for (RespMsg p : list) {
				JSONObject user = friends.getJSONObject(p.getUserId());
				p.setUser(new PlurkUser(user));
			}
		} catch (JSONException e) {
			logger.error(e.getMessage() + "-" + resps);
		}
		return list;
	}


	public static List<PlurkUser> toPlurkUser(JSONArray users) {
		List<PlurkUser> list = new ArrayList<PlurkUser>();		
		try {
			PlurkUser user = null;
			for (int i = 0; i < users.length(); i++) {
				user = new PlurkUser(users.getJSONObject(i));
				list.add(user);
			}
		} catch (JSONException e) {			
			logger.error(e.getMessage() + "-" + users);
		}
		return list;
	}
	
	public static List<PlurkAlert> toAlerts(JSONArray alerts){
		List<PlurkAlert> list = new ArrayList<PlurkAlert>();		
		try {
			PlurkAlert alert = null;
			for (int i = 0; i < alerts.length(); i++) {
				alert = new PlurkAlert(alerts.getJSONObject(i));
				list.add(alert);
			}
		} catch (JSONException e) {			
			logger.error(e.getMessage() + "-" + alerts);
		}
		return list;
	}
	
	public static List<PlurkBase> getLatestPlurk(List<PlurkBase> list,int min){
		final int minute = min;
		Predicate<PlurkBase> predicate = new Predicate<PlurkBase>() {
			
		    public boolean apply(PlurkBase plurk) {
		    	Date now = new Date();
		        try {
					return plurk.getPosted().getTime()> now.getTime() - minute*60*1000;
				} catch (JSONException e) {
				} 
				return false;
		    }
		}; 
		
		return (List<PlurkBase>) PlurkFilter.filter(list, predicate);
	}

}
