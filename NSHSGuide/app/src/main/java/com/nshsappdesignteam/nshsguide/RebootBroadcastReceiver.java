package com.nshsappdesignteam.nshsguide;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.nshsappdesignteam.nshsguide.util.Notifications;

public class RebootBroadcastReceiver extends WakefulBroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.i("RebootBroadcastReceiver", "called");

		//reset all the block notifications if appropriate
		Notifications.SINGLETON.resetBlocks();
		Notifications.SINGLETON.resetSpecialScheduleNotifications();
	}
}
