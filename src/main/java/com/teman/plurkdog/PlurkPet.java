package com.teman.plurkdog;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.teman.plurkdog.service.PlurkBotService;
import com.teman.plurkdog.strategy.ReadStrategy;

public class PlurkPet extends TimerTask{
    
    private static Timer timer = null;
    private static final String STOP = "STOP";
    private static final String RUNNING = "RUNNING";
    private static String status = STOP;
    private static int rssRunTimes ;
    private static int execInterval ;
    private static int mkFriendTime ; 
    private static int runCount = 1;
    private static PlurkPet pet = null;
    public  static Log logger = LogFactory.getLog(PlurkPet.class);
    
    PlurkBotService botService = null;
    
    
    private PlurkPet() {    	
    	botService = new PlurkBotService(new ReadStrategy());
    	DogSetting setting = DogSetting.getInstance();
    	rssRunTimes =  setting.getInt("rssRunTimes"); 
        execInterval = setting.getInt("execInterval"); // sec
        mkFriendTime =  setting.getInt("mkFriendTime"); // 60 * 3 times; 
        runCount  =  setting.getInt("runCount"); // 60 * 3 times; 
	}
    
    public static void main(String[] args) {
       PlurkPet.start();
       
    }

    @Override
    public void run() {
    	
    //	botService.setStrategy(new ReadStrategy());
		try {
			//檢查登入與網路
			botService.checkLogin();
			//回復通知訊息
			if (runCount % rssRunTimes == 0) {
				botService.procAlert();
				
			}
			//加朋友
			if( runCount % (mkFriendTime) == 0){
				botService.searchAndMakeFriend();
			}
			//處理噗文
			botService.processNewPlurks();
			//處理回應
			botService.processUnRead();
			//處理RSS
			if (runCount % rssRunTimes == 0) {
				botService.processUserData(1 * rssRunTimes);
			}
			runCount++;
		} catch (Exception e) {
			logger.error(e, e);
		}
    }
    
    
    /**
     * Start bot
     */
    public static void start() {
        if (timer != null) {
            return;
        }
        timer = new Timer();
        try {
            pet = new PlurkPet();
            timer.schedule(pet, new Date(), execInterval *1000 * 1);
            status = RUNNING;
             
        } catch (Throwable th) {
            status = STOP;
            th.printStackTrace();
            logger.error(th, th);
        }
        
    }
    
    /**
     * Stop bot
     */
    public static void stop() {        
        status = STOP;
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
                //pet.barkService.bark(Qualifier.IS,"(zzzZZZ...) ");
                logger.info("Stop SysMonitor...(OK)");
            }
        } catch (Exception e) {
        	 logger.error(e,e);            
        }
    }
}
