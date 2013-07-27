package com.teman.plurkdog.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.jplurk.CommentBy;
import com.google.jplurk.DateTime;
import com.google.jplurk.Lang;
import com.google.jplurk.Qualifier;
import com.google.jplurk.exception.PlurkException;
import com.teman.plurkdog.PlurkUtils;
import com.teman.plurkdog.bean.PlurkAlert;
import com.teman.plurkdog.bean.PlurkMsg;
import com.teman.plurkdog.bean.PlurkUser;
import com.teman.plurkdog.bean.PlurkUserProfile;
import com.teman.plurkdog.bean.RespMsg;
import com.teman.plurkdog.strategy.Strategy;

public class BaseService {

	public static Log logger = LogFactory.getLog(BaseService.class);
	
	Strategy strategy;

	UserService userService = null;
	
	public BaseService() {
		super();
		userService = UserService.getInstance();
		if (!userService.isLogin()) {
			userService.login("little_dog", "little123");
		}
	}
	
	public void checkLogin(){
		logger.info("[Check Login]....");
		if (!userService.isLogin()) {
			logger.info("[Login].... False");
			userService.login("little_dog", "little123");
		}else{
			logger.info("[Login].... True");
		}
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * 取得使用者資料
	 * @param uid
	 * @return
	 */
	public PlurkUserProfile getUserProfile(int uid){
		JSONObject resp = null;
		PlurkUserProfile userProfile = null;		
		try {
			logger.info("[Public Profile : " + uid + "]....");
			resp = userService.getClient().getPublicProfile(uid+"");
			userProfile = new PlurkUserProfile(resp);
			logger.debug(resp);
		} catch (Exception e) {
			logger.error(e, e);
		}
		return userProfile;
	}
	
	/**
	 * 取得某uid使用者的朋友列表
	 * @return
	 */
	public List<PlurkUser> fetcheFriends(int uid, int offset) {
		JSONArray friends = null;
		List<PlurkUser> list = null;
		try {
			logger.info("[Fetche Friends]....");
			friends = userService.getClient().getFriendsByOffset(
					uid+"", offset);
			list = PlurkUtils.toPlurkUser(friends);
	
			logger.info("Friends counts:" + list.size());
			logger.debug(friends);
		} catch (Exception e) {
			logger.error(e, e);
		}
		return list;
	}
	
	/**
	 * 取得新的噗文
	 * @return
	 */
	public List<PlurkMsg> getNewPlurks(int count) {
		JSONObject newPlurks = null;
		List<PlurkMsg> list = null;
		try {
			logger.info("[NewPlurks]....");
			// newPlurks =
			// userService.getClient().getPollingPlurks(DateTime.now(), 20);
			newPlurks = userService.getClient().getPlurks(DateTime.create(0),
					count, false, false, false);
			list = (List<PlurkMsg>) PlurkUtils.toPlurks(newPlurks);
	
			logger.info("Plurk counts:" + list.size());
			logger.debug(newPlurks);
		} catch (Exception e) {
			logger.error(e, e);
		}
		return list;
	}

	/**
	 * 取得未讀噗文
	 */
	public List<PlurkMsg> getUnRead(int count) {
		List<PlurkMsg> list = null;
		JSONObject unReadPlurks = null;
		try {
			logger.info("[UnRead]....");
			// read unreade plurks
			unReadPlurks = userService.getClient().getUnreadPlurks(
					DateTime.now(), count);
			// unReadPlurks = userService.getClient().getPollingUnreadCount();
			list = (List<PlurkMsg>) PlurkUtils.toPlurks(unReadPlurks);
	
			logger.info("UnReader counts:" + list.size());
			logger.debug(unReadPlurks);
		} catch (Exception e) {
			logger.error(e, e);
		}
		return list;
	}

	/**
	 * 取得某PlurkId的回應
	 * 
	 * @param plurkId
	 */
	public List<RespMsg> getResponses(String plurkId) {
		JSONObject resps = null;
		List<RespMsg> list = null;
		try {
			logger.info("[Responses]....");
			resps = userService.getClient().responseGet(plurkId);
			list = (List<RespMsg>) PlurkUtils.toResps(resps);
	
			logger.info("responses counts:" + list.size());
			logger.debug(resps);
		} catch (Exception e) {
			logger.error(e, e);
		}
		return list;
	}

	/**
	 * 標示已經閱讀
	 * 
	 * @param plurkId
	 * @return
	 */
	public boolean markAsRead(String plurkId) {
		JSONObject resp = null;
		try {
			logger.info("[Mark as Read]....");
			resp = userService.getClient().markAsRead(plurkId);
			logger.debug(resp);
			return resp.getString("success_text").equals("ok");
		} catch (JSONException e) {
			logger.error(e.getMessage());
		} catch (PlurkException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	/**
	 * 標示消音
	 * 
	 * @param plurkId
	 * @return
	 */
	public boolean mute(String plurkId) {
		JSONObject resp = null;
		try {
			logger.info("[Mute ]....");
			resp = userService.getClient().mutePlurks(plurkId);
			logger.info(resp);
			return resp.getString("success_text").equals("ok");
		} catch (JSONException e) {
			logger.error(e.getMessage());
		} catch (PlurkException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	/**
	 * 確認為朋友
	 * @param uid
	 * @return
	 */
	public boolean addAsFriend(int uid) {
		JSONObject resp = null;
		try {
			logger.info("[add as Friend ]....");
			resp = userService.getClient().addAsFriend(uid);
			logger.info(resp);
			return resp.getString("success_text").equals("ok");
		} catch (JSONException e) {
			logger.error(e);
		} catch (PlurkException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 送出朋友邀請
	 * @param uid
	 * @return
	 */
	public boolean becomeFriend(int uid) {
		JSONObject resp = null;
		try {
			logger.info("[Make Friend ]...."+uid);
			resp = userService.getClient().becomeFriend(uid);
			logger.info(resp);
			return resp.getString("success_text").equals("ok");
		} catch (JSONException e) {
			logger.error(e);
		} catch (PlurkException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 加全部為朋友
	 * @return
	 */
	public boolean makeAllFriends() {
		JSONObject resp = null;
		try {
			logger.info("[Make All Friends]....");
			resp = userService.getClient().addAllAsFriends();
			logger.info(resp);
			return resp.getString("success_text").equals("ok");
		} catch (JSONException e) {
			logger.error(e);
		} catch (PlurkException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * 加全部粉絲
	 * @return
	 */
	public boolean makeAllFans() {
		JSONObject resp = null;
		try {
			logger.info("[Make All Fans]....");
			resp = userService.getClient().addAllAsFan();
			logger.info(resp);
			return resp.getString("success_text").equals("ok");
		} catch (JSONException e) {
			logger.error(e);
		} catch (PlurkException e) {
			logger.error(e);
		}
		return false;
	}

	/**
	 * @param list
	 * @throws JSONException
	 */
	public void showDialog(List<?> list) throws JSONException {
		for (Object msg : list) {
			logger.info(msg);
		}
	}

	/**
	 * 檢查取得通知
	 */
	public List<PlurkAlert> checkAlert() {
		JSONArray resp = null;
		List<PlurkAlert> list = null;
		try {
			logger.info("[Check Alert]....");
			resp = userService.getClient().getActive();
			// resp = userService.getClient().getHistory();
			list = (List<PlurkAlert>) PlurkUtils.toAlerts(resp);
			logger.info("alert counts:" + list.size());
			logger.debug(resp);
		} catch (Exception e) {
			logger.error(e,e);
		}
		return list;
	}

	/**
	 * 發噗文
	 * @param qu
	 * @param content
	 * @throws JSONException 
	 * @throws PlurkException 
	 */
	public PlurkMsg doPlurk(Qualifier qu, String content) throws JSONException, PlurkException {
		logger.info("[Plurk]....");
		JSONObject resp = null;		 
		resp = userService.getClient().plurkAdd(content, qu,Lang.tr_ch );
		logger.debug(resp);
		return new PlurkMsg(resp);
	}

	
	/**
	 * 私噗
	 * 
	 * @param qu
	 * @param content
	 * @param limited
	 * @return
	 * @throws JSONException
	 * @throws PlurkException
	 */
	public PlurkMsg doPlurk(Qualifier qu, String content, int[] limited)
			throws JSONException, PlurkException {
		logger.info("[Plurk]....");
		JSONObject resp = null;
		resp = userService.getClient().plurkAdd(content, qu,
				new JSONArray(limited).toString(), CommentBy.All, Lang.tr_ch);
		logger.debug(resp);
		return new PlurkMsg(resp);
	}

	/**
	 * 回應噗文
	 * @param qu
	 * @param plurkId
	 * @param message
	 * @return
	 * @throws PlurkException 
	 */
	public RespMsg doRePlurk(Qualifier qu, String plurkId, String message) throws PlurkException {
		logger.info("[RePlurk]....");		
		JSONObject resp = null;		 
		resp = userService.getClient().responseAdd(plurkId, message, qu);
		logger.debug(resp);
		return new RespMsg(resp);		
	}

}