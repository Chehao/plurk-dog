package com.teman.plurkdog;

import java.util.List;

import org.json.JSONException;

import com.google.jplurk.Qualifier;
import com.google.jplurk.exception.PlurkException;
import com.teman.plurkdog.bean.PlurkAlert;
import com.teman.plurkdog.bean.PlurkMsg;
import com.teman.plurkdog.bean.PlurkUser;
import com.teman.plurkdog.bean.PlurkUserProfile;
import com.teman.plurkdog.bean.RespMsg;
import com.teman.plurkdog.service.PlurkBotService;

public class PlurkBotServiceTest extends TestBase {

	
	public PlurkBotServiceTest(String name) {
		super(name);
	}

	public void testGetUserProfile(){
		PlurkUserProfile user = botService.getUserProfile(4103958);
		System.out.println(user);
	}
	
	public void testFetcheFriends() throws JSONException{
		List<PlurkUser> list = botService.fetcheFriends(4103958,0);
		botService.showDialog(list);
		list = botService.fetcheFriends(4103958,5);
		botService.showDialog(list);
	}
	
	/*public void testFetcheMyFriends() throws JSONException{
		List<PlurkUser> list = botService.fetcheMyFriends();
		botService.showDialog(list);
	}*/
	
	/*public void testSearchAndMakeFriend() throws JSONException{
		botService.searchAndMakeFriend();
	}*/

	public void testProcessNewPlurks() throws JSONException{
		List<PlurkMsg> list = botService.getNewPlurks(50);
		botService.showDialog(list);
	}
	public void testProcessUnRead() throws JSONException{
		List<PlurkMsg> list = botService.getUnRead(50);
		botService.showDialog(list);
		
	}
	
	public void testProcessResponsed() throws JSONException{
		List<RespMsg> list = botService.getResponses("346767675");
		botService.showDialog(list);
	}
	
	public void testMarkAsRead(){
		assertEquals(true,botService.markAsRead("346767675"));
	}
	
	
	
	 public void testCheckAlert() throws JSONException{
		List<PlurkAlert> list = botService.checkAlert();
		botService.showDialog(list);
	} 
	
	/*public void testDoPlurk() throws JSONException{		
		 PlurkMsg msg = botService.doPlurk(Qualifier.SAYS, "讚了!",new int[]{4052569});
		System.out.println(msg); 
		
	}*/
	
	public void testRePlurk() throws PlurkException{
		RespMsg remsg = botService.doRePlurk(Qualifier.SAYS, "350574787", "還好啦");
		System.out.println(remsg);
	}
	
	public void testMute(){
		botService.mute("350574787");
	}
	
	public void testProcessUserData(){
		((PlurkBotService)botService).processUserData(10);
	}
	
	/*public void testProcessAlert(){
		botService.procAlert();
	}*/
	
	/*public void testBecomeFriend(){
		botService.becomeFriend(4052569);
	}*/
	
	/*public void testMakeFriend(){
		botService.addAsFriend(4052569);
	}*/ 
	
	/*public void testMakeAllFriends(){
		botService.makeAllFriends();
	}
	
	public void testMakeAllFans(){
		botService.makeAllFans();
	}*/
	
	
}
