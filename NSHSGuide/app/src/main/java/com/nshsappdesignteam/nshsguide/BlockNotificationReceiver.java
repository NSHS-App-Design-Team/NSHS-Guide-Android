package com.nshsappdesignteam.nshsguide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.util.Log;

import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.ScheduleType;
import com.nshsappdesignteam.nshsguide.util.Notifications;
import com.nshsappdesignteam.nshsguide.util.Settings;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;

import org.joda.time.LocalDate;

public class BlockNotificationReceiver extends BroadcastReceiver
{
	public static final String BLOCK_KEY = "block";
	public static final String END_OF_DAY_KEY = "endOfDay";
	public static final String SPECIAL_SCHEDULE_KEY = "specialSchedule";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		LocalDate today = LocalDate.now();

		//check to see if this is a call to kill the block notification
		if (intent.getBooleanExtra(END_OF_DAY_KEY, false)) {
			Notifications.SINGLETON.cancelWithId(Notifications.ID_BLOCK);
			Log.i("BlockNotiReceiver", "Day ended");
			return;
		}
		//check if only blocks from special schedules should be shown
		ScheduleType scheduleType = Settings.SINGLETON.getScheduleTypeForDay(today);
		if (!intent.getBooleanExtra(SPECIAL_SCHEDULE_KEY, false) && scheduleType == ScheduleType.Special) {
			Log.i("BlockNotiReceiver", "Normal block notifications ignored for special schedule blocks");
			return;
		}
		if (scheduleType == ScheduleType.NoSchool)
			return;
		if (tryResetForDaylightSavings())
			return;

		String blockString = intent.getStringExtra(BLOCK_KEY);
		Block block = Block.fromString(blockString);

		Pair<String, String> nameAndRoomNum = TeachersManager.SINGLETON.teacherTextAndRoomNumForBlock(block);
		String title = nameAndRoomNum.first;
		String content;
		String info = "";
		String subText = "";

		//give our notification layout the info we received
		String roomNum = nameAndRoomNum.second;
		if (block.isLunchBlock)
		{
			//find the start/end time of lunch block (default to 1st lunch)
			int lunchNum = Settings.SINGLETON.getLunchNumForBlockLetter(block.letter);
			Block lunch = Block.createLunchAsBlock(lunchNum, block);
			String lunchText = TeachersManager.SINGLETON.LUNCHES.get(lunchNum - 1);
			content = lunchText;

			String timeAndRoomNum = block.getTimeString();
			if (!roomNum.isEmpty())
				timeAndRoomNum += ", " + roomNum;
			info = timeAndRoomNum;

			subText = lunch.getTimeString();
		}
		else
		{
			content = block.getTimeString();
			if (!roomNum.isEmpty())
				info = roomNum;
		}

		Notifications.SINGLETON.createNextBlock(block.letter, title, content, info, subText);
		Log.i("BlockNotiReceiver", "Block notification sent for block:\n" + blockString);
	}
	private boolean tryResetForDaylightSavings()
	{
		if (Settings.SINGLETON.daylightSavingChanged())
		{
			Notifications.SINGLETON.resetBlocks();
			return true;
		}
		return false;
	}
}
