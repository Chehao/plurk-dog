package com.teman.plurkdog;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.teman.plurkdog.service.UserService;

public class AllTest {

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		TestSuite test = new TestSuite();
		test.addTestSuite(UserServiceTest.class);
		test.addTestSuite(PlurkUtilsTest.class);
		test.addTestSuite(PlurkBotServiceTest.class);
		return test; 
	}
	
}
