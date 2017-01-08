package com.androidsaper.android.main;

import android.app.Application;

import com.androidsaper.android.gui.AdtSoundEffects;


public class SaperApp extends Application {
	@Override
	public void onCreate() {
		try {
			AdtController bc = new AdtController();
			bc.SetSoundEffectPlayer(new AdtSoundEffects(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onCreate();
	}
}
