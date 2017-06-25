package com.nshsappdesignteam.nshsguide.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.nshsappdesignteam.nshsguide.BlockNotificationReceiver;
import com.nshsappdesignteam.nshsguide.helper.Block;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;

public class BlockNotifications
{
	private final Context context;
	private final AlarmManager alarmManager;
	private final int timeAdjust;
	@Nullable
	private LocalDate specialScheduleDate;
	private boolean cancelNotifications = false;

	public BlockNotifications(Context context)
	{
		this.context = context;
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		timeAdjust = Settings.SINGLETON.getBlockNotificationsTimeAdjust();
	}
	public BlockNotifications withSpecialScheduleDate(LocalDate date)
	{
		specialScheduleDate = date;
		return this;
	}
	public BlockNotifications cancelNotifications()
	{
		cancelNotifications = true;
		return this;
	}
	public void set()
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			@WorkerThread
			protected Void doInBackground(Void... params)
			{
				if (specialScheduleDate != null)
					setAlarmsForSpecialSchedule();
				else
					for (int dayOfWeek = DateTimeConstants.MONDAY; dayOfWeek <= DateTimeConstants.FRIDAY; dayOfWeek++)
						setAlarmsForDayOfWeek(dayOfWeek);

				if (cancelNotifications)
					Notifications.SINGLETON.cancelWithId(Notifications.ID_BLOCK);
				logFinished();
				return null;
			}
		}.execute(null, null, null);
	}
	private void setAlarmsForSpecialSchedule()
	{
		List<Block> blocks = Settings.SINGLETON.getSpecialScheduleForDay(specialScheduleDate);
		setAlarmsForBlocksWithDate(blocks, specialScheduleDate);
	}
	private void setAlarmsForDayOfWeek(int dayOfWeek)
	{
		List<Block> blocks = Block.BLOCKS_GIVEN_DAY.get(dayOfWeek);
		setAlarmsForBlocksWithDate(blocks, LocalDate.now().withDayOfWeek(dayOfWeek));
	}
	private void setAlarmsForBlocksWithDate(List<Block> blocks, LocalDate dateOfFirstNotification)
	{
		for (Block block : blocks)
		{
			Intent intent = getBlockIntent(block);
			if (specialScheduleDate != null)
				intent.putExtra(BlockNotificationReceiver.SPECIAL_SCHEDULE_KEY, true);
			//VOCAB: PendingIntent = intent to be called by the system, not by our app, some time in the future.
			PendingIntent pendingIntent = getPendingIntentFromIntent(intent, getBlockUniqueId(block));

			if (cancelNotifications)
			{
				alarmManager.cancel(pendingIntent);
			}
			else
			{
				LocalTime startTime = getStartTimeOfBlock(block);
				DateTime dateTime = dateOfFirstNotification.toDateTime(startTime);
				setAlarmWithDateTimeAndPendingIntent(dateTime, pendingIntent);
			}
		}

		//intent at the end of the day telling BlockNotificationsReceiver to hide the notification
		Block lastBlock = blocks.get(blocks.size() - 1);
		LocalTime endTime = getEndTimeOfBlock(lastBlock);
		DateTime dateTime = dateOfFirstNotification.toDateTime(endTime);
		PendingIntent pendingIntent = getEndOfDayPendingIntent(getEndOfDayBlockUniqueId(lastBlock));

		if (cancelNotifications)
			alarmManager.cancel(pendingIntent);
		else
			setAlarmWithDateTimeAndPendingIntent(dateTime, pendingIntent);
	}
	private void setAlarmWithDateTimeAndPendingIntent(DateTime dateTime, PendingIntent pendingIntent)
	{
		if (specialScheduleDate != null)
			alarmManager.set(AlarmManager.RTC, dateTime.getMillis(), pendingIntent);
		else
			alarmManager.setRepeating(AlarmManager.RTC, dateTime.getMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
	}
	private LocalTime getStartTimeOfBlock(Block block)
	{
		//accounts for timeAdjust (# of minutes before block starts when the notification should appear), set by the user.
		return new LocalTime(block.startTime.hour, block.startTime.minute).minusMinutes(timeAdjust);
	}
	private LocalTime getEndTimeOfBlock(Block block)
	{
		return new LocalTime(block.endTime.hour, block.endTime.minute);
	}
	private Intent getBlockIntent(Block block)
	{
		Intent intent = new Intent(context, BlockNotificationReceiver.class);
		intent.putExtra(BlockNotificationReceiver.BLOCK_KEY, block.toString());
		return intent;
	}
	//ID given to pendingIntents to distinguish one from another, preventing overlaps, allowing selected deletions
	private int getBlockUniqueId(Block block)
	{
		if (specialScheduleDate != null)
			return block.hashCode() * 31;   //arbitrary # picked to ensure difference between special schedule block hashes & normal block hashes
		else
			return block.hashCode();
	}
	//end of day block must have different unique ID, since it's based on the last block of the day
	private int getEndOfDayBlockUniqueId(Block block)
	{
		if (specialScheduleDate != null)
			return block.hashCode() * 15 * 31;   //arbitrary #s picked to ensure difference between special schedule block hashes & normal block hashes
		else
			return block.hashCode() * 15;
	}
	private PendingIntent getPendingIntentFromIntent(Intent intent, int uniqueId)
	{
		return PendingIntent.getBroadcast(context, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	private PendingIntent getEndOfDayPendingIntent(int uniqueId)
	{
		Intent intent = new Intent(context, BlockNotificationReceiver.class);
		intent.putExtra(BlockNotificationReceiver.END_OF_DAY_KEY, true);
		return PendingIntent.getBroadcast(context, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	private void logFinished()
	{
		String message;
		if (specialScheduleDate != null)
			message = cancelNotifications ? "Special schedule alarms cancelled" : "Special schedule alarms set";
		else
			message = cancelNotifications ? "Block notifications alarms cancelled" : "Block notifications alarms set";
		Log.i("BlockNotifications", message);
	}
}
