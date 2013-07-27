package com.teman.plurkdog;

import java.util.Date;


public class PlurkPetListener {

	public void PlurkPetListener() {
	}

	public static void startService(String args[]) {
		try {
			System.out.println(new Date() + " : Start Bot Service!");
			PlurkPet.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void stopService(String args[]) {
		System.out.println(new Date() + " : Stop Bot Service!");
		try {
			Thread.sleep(3000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PlurkPet.stop();

	}
}
