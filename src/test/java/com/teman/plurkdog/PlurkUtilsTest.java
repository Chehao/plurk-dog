package com.teman.plurkdog;

import java.util.List;

import org.json.JSONException;

import junit.framework.TestCase;

import com.teman.plurkdog.bean.PlurkBase;
import com.teman.plurkdog.service.PlurkBotService;

public class PlurkUtilsTest extends TestBase {

	 
	public PlurkUtilsTest(String methodName) {
		super(methodName);
		// TODO Auto-generated constructor stub
	}

	 

	public void testFilterPlurks() throws JSONException{
		List list = botService.getNewPlurks(50);
		List<PlurkBase> result = PlurkUtils.getLatestPlurk(list,30);//30分鐘
		botService.showDialog(result);
	}
}
