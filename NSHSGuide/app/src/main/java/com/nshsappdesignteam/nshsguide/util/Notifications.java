package com.nshsappdesignteam.nshsguide.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.nshsappdesignteam.nshsguide.MainActivity;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

public final class Notifications
{
	@IntDef({ID_ABSENT_LIST_UPDATE, ID_BLOCK, ID_NEW_ANNOUNCEMENT, ID_TEACHER_REQUEST})
	@Retention(RetentionPolicy.SOURCE)
	public @interface NotificationId {}
	public static final int ID_ABSENT_LIST_UPDATE = 1;
	public static final int ID_BLOCK = 2;
	public static final int ID_NEW_ANNOUNCEMENT = 3;
	public static final int ID_TEACHER_REQUEST = 4;

	public static Notifications SINGLETON;
	private final Context context;

	private Notifications(Context context)
	{
		this.context = context;
	}
	public static void createSingleton(Context context)
	{
		SINGLETON = new Notifications(context);
	}

	public void cancelWithId(@NotificationId int notificationId)
	{
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notificationId);
	}
	public void createGeneral(String title, String info, @NotificationId int notificationId, String savedLandingPage)
	{
		NotificationCompat.Builder builder = createBasicBuilder(savedLandingPage);
		builder.setSmallIcon(R.drawable.ic_notification_icon);
		builder.setContentTitle(title);
		builder.setContentText(info);
		builder.setAutoCancel(true);

		if (Settings.SINGLETON.getNotificationVibrateOn())
			vibrate();
		if (Settings.SINGLETON.getNotificationLightOn())
			builder.setDefaults(Notification.DEFAULT_LIGHTS);

		Notification notification = builder.build();
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(notificationId, notification);
		Log.i("Notifications", "sent!");
	}
	private void vibrate()
	{
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);
	}
	public void createNextBlock(String blockLetter, String title, String content, @NonNull String info, @NonNull String subText)
	{
		NotificationCompat.Builder builder = createBasicBuilder(Tabs.SINGLETON.SAVED_BLOCKS);
		builder.setSmallIcon(getSmallIconForBlockLetter(blockLetter));
		builder.setLargeIcon(getLargeIconForBlockLetter(blockLetter));
		builder.setContentTitle(title);
		builder.setContentText(content);
		if (!info.isEmpty())
			builder.setContentInfo(info);
		if (!subText.isEmpty())
			builder.setSubText(subText);
		builder.setShowWhen(false);
		builder.setOngoing(true);

		Notification notification = builder.build();
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(ID_BLOCK, notification);
	}
	private NotificationCompat.Builder createBasicBuilder(String savedLandingPage)
	{
		//let the notification open our app
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.putExtra(MainActivity.START_WITH_FRAGMENT_KEY, savedLandingPage);
		PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setVisibility(Notification.VISIBILITY_PUBLIC);
		builder.setContentIntent(pendingNotificationIntent);
		return builder;
	}
	private int getSmallIconForBlockLetter(String blockLetter)
	{
		if (blockLetter.equals(Block.BLOCK_LETTERS.get(0)))
			return R.drawable.notification_icon_a;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(1)))
			return R.drawable.notification_icon_b;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(2)))
			return R.drawable.notification_icon_c;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(3)))
			return R.drawable.notification_icon_d;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(4)))
			return R.drawable.notification_icon_e;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(5)))
			return R.drawable.notification_icon_f;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(6)))
			return R.drawable.notification_icon_g;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(7)))
			return R.drawable.notification_icon_hr;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(8)))
			return R.drawable.notification_icon_j;

		return R.drawable.notification_icon_s;
	}
	private Bitmap getLargeIconForBlockLetter(String blockLetter)
	{
		int largeIconSize = context.getResources().getDimensionPixelSize(R.dimen.notification_large_icon_size);
		Bitmap bitmap = Bitmap.createBitmap(largeIconSize, largeIconSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		int colorId = Color.colorGivenLetter(blockLetter);
		int color = ContextCompat.getColor(context, colorId);

		//draw bg
		Rect rect = new Rect(0, 0, largeIconSize, largeIconSize);
		RectF rectFloatingPoints = new RectF(rect);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawOval(rectFloatingPoints, paint);

		//draw text
		Paint textPaint = new Paint();
		textPaint.setColor(android.graphics.Color.WHITE);
		textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.notification_block_image_text_size));
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Paint.Align.CENTER);
		Rect textBounds = new Rect();
		textPaint.getTextBounds(blockLetter, 0, blockLetter.length(), textBounds);
		canvas.drawText(blockLetter, rect.exactCenterX(), rect.exactCenterY() - textBounds.exactCenterY(), textPaint);

		return bitmap;
	}
	public void resetSpecialScheduleNotifications()
	{
		Set<String> dates = Settings.SINGLETON.getSpecialScheduleDates();
		BlockNotifications blockNotifications = new BlockNotifications(context);

		for (String dateString : dates)
		{
			LocalDate date = LocalDate.parse(dateString, DateTimeFormat.forPattern(Internet.SPECIAL_SCHEDULE_DATE_PATTERN));
			blockNotifications
					.withSpecialScheduleDate(date)
					.set();
		}
	}
	public void resetBlocks()
	{
		if (Settings.SINGLETON.getBlockNotificationsOn())
			Notifications.SINGLETON.setBlocks();
	}
	public void toggleBlocks()
	{
		boolean shouldTurnOn = Settings.SINGLETON.getBlockNotificationsOn();

		if (shouldTurnOn)
		{
			//if we should turn it on and it hasn't been set before, set it
			if (!Settings.SINGLETON.getNotificationsSet())
			{
				setBlocks();
				Settings.SINGLETON.setNotificationsSet(true);
			}
		}
		else
		{
			//if we should turn it off and it has been set already, kill it
			if (Settings.SINGLETON.getNotificationsSet())
			{
				destroyBlocks();
				Settings.SINGLETON.setNotificationsSet(false);
			}
		}

	}
	public void setBlocks()
	{
		new BlockNotifications(context)
				.set();
	}
	public void destroyBlocks()
	{
		new BlockNotifications(context)
				.cancelNotifications()
				.set();
	}
	public void setSpecialScheduleBlocks(LocalDate date)
	{
		new BlockNotifications(context)
				.withSpecialScheduleDate(date)
				.set();
	}
	public void destroySpecialScheduleBlocks(LocalDate date)
	{
		new BlockNotifications(context)
				.withSpecialScheduleDate(date)
				.cancelNotifications()
				.set();
	}
}
