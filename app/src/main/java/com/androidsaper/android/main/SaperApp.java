package com.androidsaper.android.main;

import android.app.Application;


public class SaperApp extends Application {
	@Override
	public void onCreate() {
		try {
			AdtController bc = new AdtController();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onCreate();
	}
}
