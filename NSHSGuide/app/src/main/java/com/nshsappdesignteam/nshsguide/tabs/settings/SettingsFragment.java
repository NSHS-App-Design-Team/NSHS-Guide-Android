package com.nshsappdesignteam.nshsguide.tabs.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.util.Notifications;
import com.nshsappdesignteam.nshsguide.util.Settings;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	@Override
	public void onResume()
	{
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	@Override
	public void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		Preference preference = findPreference(key);

		if (preference instanceof ListPreference)
		{
			ListPreference listPref = (ListPreference) preference;
			//shows current selected string entry of listPreference
			preference.setSummary(listPref.getEntry());
		}
		else if (preference instanceof IntListPreference)
		{
			IntListPreference intListPref = (IntListPreference) preference;
			preference.setSummary(intListPref.getEntry());
		}

		additionalActionForKey(key);
	}

	private void additionalActionForKey(String key)
	{
		//Note: cannot use switch statement because getString() isn't a "constant expression"
		if (key.equals(getString(R.string.key_block_notifications_on)))
			Notifications.SINGLETON.toggleBlocks();
		else if (key.equals(getString(R.string.key_block_notifications_time_adjust)))
			changeBlockNotificationTimeAdjust();
	}
	private void changeBlockNotificationTimeAdjust()
	{
		//only reset notifications if block notifications are on
		if (Settings.SINGLETON.getBlockNotificationsOn())
		{
			Notifications.SINGLETON.destroyBlocks();
			Notifications.SINGLETON.setBlocks();
		}
	}
}
