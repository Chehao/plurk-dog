package com.teman.plurkdog.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import com.google.jplurk.Qualifier;
import com.teman.plurkdog.RSSConfig;
import com.teman.plurkdog.bean.PlurkMsg;
import com.teman.plurkdog.bean.PlurkUser;
import com.teman.plurkdog.bean.RespMsg;
import com.teman.plurkdog.service.BaseService;
import com.teman.plurkdog.service.PlurkBotService;

public class ReadStrategy implements Strategy {

	public static Log logger = LogFactory.getLog(ReadStrategy.class); 
	private static String[] say_message = { "汪汪!", "愛睏", "嗚...", "哎呀", "汪>>>>", "齁...", "惡...", "大~象..大~象","真的~~~~" };
	private static String[] think_message = { "呀呀", "對", "如你所願!", "哦..", "狗怎麼會說話，你想太多了", "我想下班啦", "(･ิ㉨ ･ิ)" };
	private static String[] do_message = { "吃飯..", "噓噓...", "拉...舒服喔", "跑....", "(搖尾巴)", "zzZZz.." };

	private static Qualifier[] qualifier = { Qualifier.SAYS, Qualifier.THINKS,
			Qualifier.IS };
	
	private static String[][] action_message = { say_message, think_message,
		do_message };
	
	public ReadStrategy(){
		 
	}
	
	@Override
	public void procPlurks(List<PlurkMsg> plurks, PlurkBotService botService) {
		
		
		for (PlurkMsg plurkMsg : plurks) {
			try {
				procOnePlurk(botService, plurkMsg);
			} catch (Exception e) {
				logger.error(e, e);
			}
			 
		}

	}

	private void procOnePlurk(PlurkBotService botService, PlurkMsg plurkMsg) throws JSONException, Exception {
		PlurkUser user =  botService.getUserService().currentUser();
		String nickName = plurkMsg.getUser().getNickName();
		logger.info(plurkMsg);
		// 動作 之後改成策略或是命令Pattern-----------------
		// 無人回應 且為訂閱feed
		if (plurkMsg.getResponseCount() == 0
				&& plurkMsg.getContent().indexOf(nickName + ":feed") > -1) {

			botService.subscribeFeed(plurkMsg.getPlurkId(), plurkMsg
					.getOwnerId(), nickName, plurkMsg.getContent());
		}			
		
		//別人撲給我，回應給噗文者
		if (plurkMsg.getResponseCount() == 0 
				&&  plurkMsg.getContent().indexOf( user.getNickName()) > -1){
			doRePlurk(plurkMsg, botService);	
		}
		
		if (plurkMsg.getResponseCount() > 0
				&& plurkMsg.getResponseCount() <= 20) {
			if (plurkMsg.getIsUnread()) {
				// 取得回應
				List<RespMsg> responses = botService
						.getResponses(plurkMsg.getPlurkId());
				procResponses(plurkMsg, responses, botService);
				// 標示已讀

				botService.markAsRead(plurkMsg.getPlurkId());
			}
		}
		// 回應數超過20 則設靜音
		if (plurkMsg.getResponseCount() > 20 && plurkMsg.getIsUnread()) {
			botService.mute(plurkMsg.getPlurkId());
		}
	}

	public String getRandomBarkMsg(String nick_name){
		Random generator = new Random();
		int actionIndex = generator.nextInt(qualifier.length);
		int messageIndex = generator
				.nextInt(action_message[actionIndex].length);
		String message = "";
		if (nick_name != null) {
			message += getReMsg(nick_name);
		}
		message += action_message[actionIndex][messageIndex];
		return message;
		
	}
	
	private String getReMsg(String nick_name){
		StringBuffer sb = new StringBuffer();
		sb.append("@");
		sb.append(nick_name);
		sb.append(": ");
		return sb.toString();
	}

	private String processFamiliar( String owner_id,
			String nickName,int point) throws Exception {
		RSSConfig rssconfig = RSSConfig.getInstance();
		String familiar = rssconfig.getFamiliar(owner_id);
		if (familiar == null) {
			rssconfig.addNewSubscribe(owner_id, "");
			rssconfig.setName(owner_id, nickName);
			familiar = rssconfig.getFamiliar(owner_id);
		}
		String total = Integer.parseInt(familiar) + point +"";
		rssconfig.setFamiliar(owner_id, total );
		rssconfig.save();
		return total;

	}

	 
	/**
	 * @param plurkMsg
	 * @param responses
	 * @param botService
	 */
	public void procResponses(PlurkMsg plurkMsg,List<RespMsg> responses, BaseService botService) {		 
		
		PlurkUser user =  botService.getUserService().currentUser();		
		logger.info("-------Response Strategry------------------");
		 	 
		String current_responser = "";
		String current_user_id = "";
		try {	
			Map<String,List<RespMsg>> info = analysisResponse(responses,user);
			List<RespMsg> myResp = info.get("MY_RESP");
			List<RespMsg> otherResp = info.get("OTHER_RESP");
			logger.info("--------my resp count vs others : " 
					+ myResp.size() + " vs. " + otherResp.size());
			
			RespMsg currentResp = null;
			
			if(!otherResp.isEmpty()){
				currentResp = otherResp.get(otherResp.size()-1);
				current_responser = currentResp.getUser().getNickName();
				current_user_id = currentResp.getUserId();
				if(currentResp.getContent().indexOf("放狗") > -1){//咬樓主
					current_responser = plurkMsg.getUser().getNickName();					
				}
				RespMsg lastBark = null;
				if(myResp.size()>0){
					lastBark = myResp.get(myResp.size()-1);
				}
				// 回應給回應的人動作	
				boolean isbark = (lastBark !=null && lastBark.getContent().indexOf(current_responser) == -1) 
					|| otherResp.size() > myResp.size();
				if(isbark){				 
					botService.doRePlurk(Qualifier.SAYS,plurkMsg.getPlurkId(), getRandomBarkMsg(current_responser));// 回他的名字				 
					processFamiliar(current_user_id,current_responser,10);		
				} 
			}

			// 回應給噗文者	
			if(myResp.size()==0 && plurkMsg.getContent().indexOf( user.getNickName()) > -1 ){				
				doRePlurk(plurkMsg, botService);		
			}
			 
			
		
		} catch (Exception e) {
			logger.error(e,e);
		}

	}

	/**
	 * @param plurkMsg
	 * @param botService
	 * @throws JSONException
	 * @throws Exception
	 */
	private void doRePlurk(PlurkMsg plurkMsg, BaseService botService)
			throws JSONException, Exception {
		String msg = getRandomBarkMsg(plurkMsg.getUser().getNickName());
		botService.doRePlurk(Qualifier.SAYS,plurkMsg.getPlurkId(), msg);
		// 處理familiar
		Random generator = new Random();
		int point = generator.nextInt(9);	
		String reMsg = 	 getReMsg(plurkMsg.getUser().getNickName());	
		if (point < 7 && point > 3) {
			String fam = processFamiliar(plurkMsg
					.getOwnerId(), plurkMsg.getUser().getNickName(),point); 
			msg =  reMsg+ "親密度上升:" + point + "!("+ fam + ")";
			botService.doRePlurk(Qualifier.SAYS,plurkMsg.getPlurkId(), msg );
			
		} else {
			String fam = processFamiliar(plurkMsg
					.getOwnerId(), plurkMsg.getUser().getNickName(),-point); 
			msg = reMsg + "親密度下降:" + -point + "!("+ fam + ")";
			botService.doRePlurk(Qualifier.SAYS,plurkMsg.getPlurkId(), msg);						 
		}
	}

	/**
	 * @param responses
	 * @param user
	 * @return
	 * @throws JSONException
	 */
	private Map<String,List<RespMsg>> analysisResponse(List<RespMsg> responses,PlurkUser user) throws JSONException {
		 
		List<RespMsg> myResp = new ArrayList<RespMsg>();
		List<RespMsg> otherResp = new ArrayList<RespMsg>();
		Map<String,List<RespMsg>> info = new HashMap<String,List<RespMsg>>();
		 
		for (RespMsg resp : responses) {
			logger.debug("resp.getUserId:" + resp.getUserId());
			logger.debug("resp.getUser().getUid():" + resp.getUser().getUid());
			
			if (resp.getUserId().equals(user.getUid()+"")) {			 
				myResp.add(resp);				
			}
			if (resp.getContent().indexOf(user.getNickName()) > -1){				 
				otherResp.add(resp);
				//current_responser = resp.getUser().getNickName();
				//current_user_id = resp.getUserId();
				/*if(resp.getContent().indexOf("放狗") > -1){//咬樓主
					current_responser = "";
				}*/				
			}		
			logger.info(resp);
		}
		
		info.put("MY_RESP", myResp);
		info.put("OTHER_RESP", otherResp);
		return info;
		
	}

}
