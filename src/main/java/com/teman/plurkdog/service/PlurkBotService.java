package com.teman.plurkdog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import com.google.jplurk.Gender;
import com.google.jplurk.PlurkClient;
import com.google.jplurk.Qualifier;
import com.google.jplurk.exception.PlurkException;
import com.sun.syndication.feed.synd.SyndEntry;
import com.teman.plurkdog.PlurkFilter;
import com.teman.plurkdog.RSSConfig;
import com.teman.plurkdog.StringUtil;
import com.teman.plurkdog.PlurkFilter.Predicate;
import com.teman.plurkdog.bean.PlurkAlert;
import com.teman.plurkdog.bean.PlurkUser;
import com.teman.plurkdog.bean.PlurkUserProfile;
import com.teman.plurkdog.rss.FeedReader;
import com.teman.plurkdog.strategy.Strategy;



/**
 * @author CheHao Hsiao
 * 
 */
public class PlurkBotService extends BaseService {

	
	public static Log logger = LogFactory.getLog(PlurkBotService.class);

	public PlurkBotService() {
		super();		
		
	}
	
	public PlurkBotService(Strategy strategy){
		this();
		this.strategy = strategy;
	}

	
	
	/**
	 * 取得自己朋友列表
	 * @return
	 * @throws JSONException 
	 */
	public List<PlurkUser> fetcheMyFriends() throws JSONException {
		return fetcheAllFriends(userService.currentUser().getUid());
	}
	
	/**
	 * 取得某uid的所有朋友，朋友數量很多會很久
	 * @param uid
	 * @return
	 * @throws JSONException
	 */
	public List<PlurkUser> fetcheAllFriends(int uid) throws JSONException {
		PlurkUserProfile userProfile =  getUserProfile(uid);
		List<PlurkUser> friends = null;
		List<PlurkUser> totalFriends = new ArrayList<PlurkUser>();
		for(int i = 0 ; i < userProfile.getFriendsCount(); i += 10){
			friends = fetcheFriends(uid, i );
			if(friends.size()==0){
				break;
			}
			totalFriends.addAll(friends);
		}		
		return totalFriends;
	}
	
	/**
	 * 處理新噗文，使用不同策略
	 */
	public void processNewPlurks() {
		this.strategy.procPlurks(getNewPlurks(50),this);
	}

	/**
	 * 處理未讀取噗文，不同策略
	 */
	public void processUnRead() {
		this.strategy.procPlurks(getUnRead(50),this);		
	}

	/**
	 * 處理通知
	 */
	public void procAlert(){
		List<PlurkAlert> list = checkAlert();
		for(PlurkAlert alert : list){
			try {
				PlurkUser alertUser = alert.getAlertUser();
				if(alert.getType().equals(PlurkAlert.NEW_FAN) 
						&& alertUser.getKarma()>60){
					becomeFriend(alertUser.getId());
				}else if(alert.getType().equals(PlurkAlert.FRIENDSHIP_REQUEST)){
					addAsFriend(alertUser.getId());
				}
			} catch (JSONException e) {
				logger.error(e);				 
			}
		}
	}

	/**
	 * 尋找朋友加入
	 * @throws JSONException
	 */
	public void searchAndMakeFriend() throws JSONException {
		//searchAndMakeFriend(userService.currentUser().getUid());
		List<PlurkUser> friends = fetcheMyFriends();
		Map<String,Object> hadMakeFriend = new LinkedMap();
		Map<String,Object> hadSearchPool = new LinkedMap();
		for(PlurkUser user : friends){
			searchAndMakeFriend(user.getUid(),hadMakeFriend,hadSearchPool);
		}
		logger.debug("=================");
		logger.info("[search and Make Friend]...." + hadMakeFriend.size());
		logger.info("[search and Make Friend]...." + hadMakeFriend);
	}
	
	/**
	 * 找朋友加入的邏輯
	 * @throws JSONException 
     * 
     */
	public void searchAndMakeFriend(int uid,Map<String,Object> hadMakeFriend,Map<String,Object> hadSearchPool) throws JSONException {
		
		//加過了
		if(hadMakeFriend.containsKey(uid+"") || hadMakeFriend.size() >= 3 ){
			return ;
		}
		PlurkUserProfile userProfile =  getUserProfile(uid);
		if(!userProfile.areFriends()){			
			logger.debug("[Will be Friend]..." + userProfile);
			becomeFriend(uid);
			hadMakeFriend.put(uid+"", userProfile);	//記錄加入		
			return;
		}else{
			logger.debug("[Already Friend]..." + userProfile);
		}
		//找過了
		if(hadSearchPool.containsKey(uid)||userProfile.getFriendsCount()<=0){
			return ;
		}		
		
		//找下一個
		PlurkUser theOne = findNextFriends(userProfile);
		hadSearchPool.put(uid+"", userProfile);//記錄找過
		
		if(theOne != null){			 		
			searchAndMakeFriend(theOne.getUid(),hadMakeFriend,hadSearchPool);
		}

	}

	/**
	 * 從一個使用者的朋友清單隨機找下個人選
	 * @param userProfile
	 * @return
	 * @throws JSONException
	 */
	private PlurkUser findNextFriends(PlurkUserProfile userProfile) throws JSONException {
		Random randomGenerator = new Random();
		int offset =randomGenerator.nextInt(userProfile.getFriendsCount());
		List<PlurkUser> friends = fetcheFriends(userProfile.getUser().getUid(),offset);		 
		friends =  filterFindUser(friends);
		PlurkUser theOne = null;
		if(!friends.isEmpty()){
			theOne = friends.get(0);		
		}
		return theOne;
	}

	/**
	 * 過濾人選條件
	 * @param friends
	 * @return
	 */
	private List<PlurkUser> filterFindUser(List<PlurkUser> friends) {
		Predicate<PlurkUser> predicate = new Predicate<PlurkUser>() {			
		    public boolean apply(PlurkUser user) {
		    	try {
					if(user.getKarma()>50 && user.getGender()==Gender.Female 
							&& user.getAvatar()<28){  
						return true;
					}
				} catch (JSONException e) {
					logger.error(e.getMessage());					
				}
				return false;
		    }
		}; 		 
		return(List<PlurkUser>) PlurkFilter.filter(friends, predicate);
	}

	/**
	 * 訂閱RSS Data
	 * @param plurk_id
	 * @param owner_id
	 * @param nickName
	 * @param content
	 * @throws Exception
	 */
	public void subscribeFeed(String plurk_id, String owner_id,
			String nickName, String content) throws Exception {
		String feed = StringUtil.findString(content, "[", "]");
		String message = "";
		String url = "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)([).!';/?:,][[:blank:]])?$";
		message += "@" + nickName + ": 汪  ";
		try {
			if (feed == null || !feed.matches(url)) {
				message += feed + "有問題";
			} else {
				RSSConfig rssconfig = RSSConfig.getInstance();
				String rssReed = rssconfig.getSubscribe(owner_id);
				if (rssReed == null) {
					rssconfig.addNewSubscribe(owner_id, feed);
					rssconfig.setName(owner_id, nickName);
				} else {
					rssconfig.setSubscribe(owner_id, feed);
				}
				rssconfig.save();
				message += " 訂閱 ok";

			}
		} catch (Exception e) {
			message += feed + "有問題:";
		}
		doRePlurk(Qualifier.SAYS,plurk_id, message);
	}

	/**
	 * 處理 RSS Data，取出訂閱文章
	 * @param inteval
	 */
	public void processUserData(int interval) {
		
		RSSConfig rssConfig = RSSConfig.getInstance();
		String[] ids = rssConfig.getUserIds();
		// 每個人
		for (int i = 0; i < ids.length; i++) {
			String rssfeed = rssConfig.getSubscribe(ids[i]);
			if (!StringUtils.isEmpty(rssfeed)) {
				logger.info("subscribe user :" + ids[i]);
				logger.info("subscribe : " + rssfeed + " ...");
				// 取得訂閱Rss 文章
				plurkRss(rssfeed, ids[i], interval,1);
			}
		}
		// 全部的
		String globalSubscribe[] = rssConfig.getGlobalSubscribe();
		for (int i = 0; i < globalSubscribe.length; i++) {
			logger.info("global subscribe : " + globalSubscribe[i] + " ...");
			if (!StringUtils.isEmpty(globalSubscribe[i])) {
				plurkRss(globalSubscribe[i], null, interval,1);
			}
		}
	}


	/**
	 * 發訂閱文章噗
	 * @param rssfeed  訂閱的連結
	 * @param subUid 訂閱使用者
	 * @param minute 最近幾分鐘的文章
	 * @param count 噗送的訂閱文章數
	 */
	private void plurkRss(String rssfeed, String subUid,int minute,  int count) {

		List<SyndEntry> entryList = fetchRss(rssfeed,minute);
		for (int j = 0; j < entryList.size(); j++) {
			String message = getRssMsgContent(entryList.get(j));
			try {
				if (subUid == null || subUid.equals(userService.currentUser().getUid())) {// 自己
					// 噗 barkService.bark(Qualifier.SHARES, message);
					this.doPlurk(Qualifier.SHARES, message);
				} else {// 其他人
					// 噗 barkService.barkToLimited(Qualifier.SHARES, message,new
					// String[]{ids[i]});
					this.doPlurk(Qualifier.SHARES, message,new int[]{Integer.parseInt(subUid)});
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e);
			} catch (JSONException e) {
				logger.error(e);
			} catch (NumberFormatException e) {
				logger.error(e);
			} catch (PlurkException e) {
				logger.error(e);
			}
			if (j > count) {
				break;
			}
		}
	}

	
	
	/**
	 * 組訂閱文章訊息噗
	 * @param entry
	 * @return
	 */
	private String getRssMsgContent(SyndEntry entry) {
		String message = "汪! " + entry.getLink() + " ("
				+ entry.getTitle().replaceAll("\\(\\)", "") + ") - "
				+ entry.getAuthor();
		return message;
	}

	/**
	 * 取得RSS文章
	 * @param rssfeed
	 * @param minute
	 * @return
	 */
	private List<SyndEntry> fetchRss(String rssfeed,int minute) {
		return FeedReader.getNewEntry(rssfeed, minute);
	}

	 

}
