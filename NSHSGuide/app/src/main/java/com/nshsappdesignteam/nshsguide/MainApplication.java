package com.nshsappdesignteam.nshsguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.nshsappdesignteam.nshsguide.util.Internet;
import com.nshsappdesignteam.nshsguide.util.Notifications;
import com.nshsappdesignteam.nshsguide.util.Settings;
import com.nshsappdesignteam.nshsguide.util.Tabs;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;

//extends MultiDexApp because we have too many dependency classes ( > 65536) so they must be compressed
public class MainApplication extends MultiDexApplication
{
	@Override
	protected void attachBaseContext(Context base)
	{
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
	@Override
	public void onCreate()
	{
		super.onCreate();

		Log.i("MainApplication", "onCreate called");
		enableEssentialServices();

		if (Settings.SINGLETON.wasCurrentVersion())
			return;
		crossVersionUpdates();
		Settings.SINGLETON.setCurrentVersion();
	}
	private void enableEssentialServices()
	{
		Settings.createSingleton(this);
		Tabs.createSingleton(this);
		Internet.createSingleton(this);
		Notifications.createSingleton(this);
		TeachersManager.createSingleton(this);
	}
	//TODO this should change dynamically with every new build depending on what needs to be changed
	private void crossVersionUpdates()
	{
		//get all preferences that shouldn't be destroyed in this update
		boolean registeredInDatabase = Settings.SINGLETON.getRegisteredInDatabase();
		String regId = Settings.SINGLETON.getRegID();
		boolean blockNotificationsOn = Settings.SINGLETON.getBlockNotificationsOn();
		boolean generalNotificationsOn = Settings.SINGLETON.getGeneralNotificationsOn();
		boolean notificationVibrateOn = Settings.SINGLETON.getNotificationVibrateOn();
		boolean notificationLightOn = Settings.SINGLETON.getNotificationLightOn();
		int blockNotificationTimeAdjust = Settings.SINGLETON.getBlockNotificationsTimeAdjust();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		//reset all settings that shouldn't be erased
		editor.putBoolean(getString(R.string.key_registered_in_database), registeredInDatabase);
		editor.putString(getString(R.string.key_reg_id), regId);
		editor.putBoolean(getString(R.string.key_block_notifications_on), blockNotificationsOn);
		editor.putBoolean(getString(R.string.key_general_notifications_on), generalNotificationsOn);
		editor.putBoolean(getString(R.string.key_notification_vibrate_on), notificationVibrateOn);
		editor.putBoolean(getString(R.string.key_notification_light_on), notificationLightOn);
		editor.putInt(getString(R.string.key_block_notifications_time_adjust), blockNotificationTimeAdjust);
		editor.putBoolean(getString(R.string.key_first_run), false);

		//destroy all other preferences
		editor.clear();
		editor.apply();
	}
}
