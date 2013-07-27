package com.teman.plurkdog;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.teman.plurkdog.service.UserService;

public class UserServiceTest extends TestCase {

	UserService userService = null;

	@Override
	public void setName(String name) {

		super.setName(name);
	}

	@Override
	protected void setUp() throws Exception {

		userService = UserService.getInstance();

		super.setUp();
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(UserServiceTest.class);
	}

	public void testLoginSuccess() {
		userService.login("little_dog", "little123");
		assertEquals(true, userService.isLogin());
	}

	public void testLoginFailure() {
		userService.logout();
		userService.login("little_dog", "123456");
		assertEquals(false, userService.isLogin());
	}

}
