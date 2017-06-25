package com.nshsappdesignteam.nshsguide;

import android.app.backup.BackupAgentHelper;

import com.nshsappdesignteam.nshsguide.util.Settings;

public class MyBackupAgent extends BackupAgentHelper
{
	@Override
	public void onRestoreFinished()
	{
		super.onRestoreFinished();
		Settings.SINGLETON.setRegisteredInDatabase(false);
		Settings.SINGLETON.setNotificationsSet(false);
	}
}
