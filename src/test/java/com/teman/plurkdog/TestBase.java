package com.teman.plurkdog;

import com.teman.plurkdog.service.BaseService;
import com.teman.plurkdog.service.PlurkBotService;

import junit.framework.TestCase;

public class TestBase extends TestCase {

	BaseService botService = null;
	public TestBase(String methodName){		
		super(methodName);	
	}
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		System.out.println("=========== start "+super.getName()+"=============");
		botService = new PlurkBotService();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		System.out.println("=========== end "+super.getName()+"=============");
	}

}
