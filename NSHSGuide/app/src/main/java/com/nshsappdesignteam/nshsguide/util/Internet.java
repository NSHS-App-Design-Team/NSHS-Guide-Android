package com.nshsappdesignteam.nshsguide.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.async.EventBoolean;
import com.nshsappdesignteam.nshsguide.async.EventGotAbsentTeachers;
import com.nshsappdesignteam.nshsguide.async.EventGotAnnouncement;
import com.nshsappdesignteam.nshsguide.async.EventSpecialSchedule;
import com.nshsappdesignteam.nshsguide.async.EventTeachersList;
import com.nshsappdesignteam.nshsguide.async.MyEventBus;
import com.nshsappdesignteam.nshsguide.helper.Callback;
import com.nshsappdesignteam.nshsguide.tabs.absentTeachers.TeacherYourAbsent;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Internet
{
	private static final String PROJECT_NUM_FOR_GCM = "1018408199795";

	//title of json to indicate its content
	private static final String TEACHER_ABSENT_LIST_KEY = "Absent Teachers";
	private static final String ANNOUNCEMENT_KEY = "Announcement";
	private static final String SPECIAL_SCHEDULE_KEY =	"Special Schedule";
	private static final String TEACHERS_LIST_KEY = "Teachers List";
	private static final String REQUEST_TEACHER_KEY = "Request Teacher";

	private static final String DEVELOPER_MODE_KEY = "Developer Mode";

	private static final String PHP_REG_ID_KEY = "regID";//key to post to the PHP webpage so it knows what info we're sending
	private static final String PHP_REG_ID_URL = "http://nshsguide.newton.k12.ma.us/ajax/set-reg-id-android.php";
	private static final String PHP_ABSENT_TEACHERS_URL = "http://nshsguide.newton.k12.ma.us/ajax/absent-teachers.php";
	private static final String PHP_ANNOUNCEMENT_URL = "http://nshsguide.newton.k12.ma.us/ajax/get-announcement.php";
	private static final String PHP_FEEDBACK_NAME_KEY = "name";
	private static final String PHP_FEEDBACK_EMAIL_KEY = "email";
	private static final String PHP_FEEDBACK_FEEDBACK_KEY = "feedback";
	private static final String PHP_FEEDBACK_URL = "http://nshsguide.newton.k12.ma.us/feedback.php";
	private static final String PHP_SPECIAL_SCHEDULE_URL = "http://nshsguide.newton.k12.ma.us/ajax/special-schedule-list.php";
	private static final String PHP_SPECIAL_SCHEDULE_DATES_KEY = "dates";
	private static final String PHP_TEACHERS_LIST_URL = "http://nshsguide.newton.k12.ma.us/ajax/get-faculty.php";
	private static final String PHP_REQUEST_TEACHER_URL = "http://nshsguide.newton.k12.ma.us/ajax/set-teacher-request-android.php";
	private static final String PHP_REQUEST_TEACHER_FIRST_NAME_KEY = "firstName";
	private static final String PHP_REQUEST_TEACHER_LAST_NAME_KEY = "lastName";

	public static final String SPECIAL_SCHEDULE_DATE_PATTERN = "yyyy-MM-dd";

	public static Internet SINGLETON;
	private final Context context;

	private Internet(Context context)
	{
		this.context = context;
	}
	public static void createSingleton(Context context)
	{
		SINGLETON = new Internet(context);
	}

	//used to see if the user is connected to the internet
	public boolean isDisconnected()
	{
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return info == null || !info.isConnectedOrConnecting();
	}
	public void performActionWithJSONAndNotify(JSONObject jsonObject, boolean showNotification)
	{
		Log.i("performActionWithJSON", "called, title = " + jsonObject.optString("title"));
		if (messageForDeveloper(jsonObject) && !Settings.SINGLETON.isDeveloperModeOn())
			return;

		switch (jsonObject.optString("title"))
		{
			case TEACHER_ABSENT_LIST_KEY:
				Settings.SINGLETON.setAbsentTeachers(jsonObject);
				//call absentTeachers() regardless of showNotification because that'll automatically update yourAbsentTeachers in Settings (for block notification)
				Set<TeacherYourAbsent> yourAbsentTeachers = TeachersManager.SINGLETON.saveNewYourAbsentTeachers();
				if (showNotification)
					notifyAbsentListUpdate(yourAbsentTeachers);
				break;
			case ANNOUNCEMENT_KEY:
				Settings.SINGLETON.setAnnouncement(jsonObject);
				if (showNotification)
					notifyAnnouncement();
				break;
			case SPECIAL_SCHEDULE_KEY:
				Settings.SINGLETON.addAndRemoveSpecialSchedules(jsonObject);
				break;
			case TEACHERS_LIST_KEY:
				Settings.SINGLETON.setTeachersList(jsonObject);
				break;
			case REQUEST_TEACHER_KEY:
				notifyTeacherRequest(jsonObject);
				break;
			default:
				Log.wtf("Internet", "weird title in performActionWithJSONAndNotify: " + jsonObject.optString("title"));
		}
	}
	private boolean messageForDeveloper(JSONObject jsonObject)
	{
		return jsonObject.optBoolean(DEVELOPER_MODE_KEY, false);
	}
	private void notifyAbsentListUpdate(Set<TeacherYourAbsent> yourAbsentTeachers)
	{
		if (!Settings.SINGLETON.getGeneralNotificationsOn())
			return;

		String title = context.getString(R.string.notification_absence_list_update);
		String info = getAbsentListInfo(yourAbsentTeachers);

		Notifications.SINGLETON.createGeneral(title, info, Notifications.ID_ABSENT_LIST_UPDATE, Tabs.SINGLETON.SAVED_ABSENT_TEACHERS);
		Log.i("Absent list update", "info: " + info);
	}
	private String getAbsentListInfo(Set<TeacherYourAbsent> yourAbsentTeachers)
	{
		if (yourAbsentTeachers.isEmpty())
			return context.getString(R.string.notification_blocks_cancelled_zero);

		//info will be formatted as so: "A, B, C " + R.string text
		StringBuilder info = new StringBuilder();
		Set<String> blockLetters = new HashSet<>(yourAbsentTeachers.size());
		for (TeacherYourAbsent teacher : yourAbsentTeachers)
			blockLetters.add(teacher.block.letter);
		Joiner.on(", ").appendTo(info, blockLetters);
		info.append(" ");
		info.append(context.getString(R.string.notification_blocks_cancelled_many));

		return info.toString();
	}
	private void notifyAnnouncement()
	{
		if (!Settings.SINGLETON.getGeneralNotificationsOn())
			return;

		String title = context.getString(R.string.notification_announcement);
		String announcement = Settings.SINGLETON.getAnnouncement();
		if (announcement != null)
		{
			Notifications.SINGLETON.createGeneral(title, announcement, Notifications.ID_NEW_ANNOUNCEMENT, Tabs.SINGLETON.SAVED_ABSENT_TEACHERS);
			Log.i("Announcement", "info: " + announcement);
		}
	}
	private void notifyTeacherRequest(JSONObject jsonObject)
	{
		if (!Settings.SINGLETON.getGeneralNotificationsOn())
			return;

		boolean approved = jsonObject.optBoolean("approved");
		String teacherName = jsonObject.optString("name");
		String title;
		if (approved)
			title = context.getString(R.string.notification_teacher_request_approved);
		else
			title = context.getString(R.string.notification_teacher_request_denied);
		String content = context.getString(R.string.notification_teacher_request_name, teacherName);
		Notifications.SINGLETON.createGeneral(title, content, Notifications.ID_TEACHER_REQUEST, Tabs.SINGLETON.SAVED_ABSENT_TEACHERS);
	}

	/*
	QUERIES & POSTS
	 */
	public void queryAbsentTeachers()
	{
		queryJSONFromURLWithData(PHP_ABSENT_TEACHERS_URL, "", new EventGotAbsentTeachers());
	}
	public void queryAnnouncement()
	{
		queryJSONFromURLWithData(PHP_ANNOUNCEMENT_URL, "", new EventGotAnnouncement());
	}
	public void querySpecialSchedules()
	{
		Set<String> specialScheduleDates = Settings.SINGLETON.getSpecialScheduleDates();
		Map<String, String> postDataMap = new HashMap<>(specialScheduleDates.size());
		for (String date : specialScheduleDates)
		{
			String specialSchedule = Settings.SINGLETON.getSpecialScheduleTextForDayText(date);
			postDataMap.put(date, specialSchedule);
		}
		postDataMap.put(PHP_SPECIAL_SCHEDULE_DATES_KEY, Joiner.on("|").join(specialScheduleDates));

		String postData = postDataFromMap(postDataMap);
		queryJSONFromURLWithData(PHP_SPECIAL_SCHEDULE_URL, postData, new EventSpecialSchedule());
	}
	public void queryTeachersList()
	{
		queryJSONFromURLWithData(PHP_TEACHERS_LIST_URL, "", new EventTeachersList());
	}
	private String postDataFromMap(Map<String, String> map)
	{
		List<String> dataParts = new ArrayList<>(map.size());
		for (Map.Entry<String, String> entry : map.entrySet())
			dataParts.add(entry.getKey() + "=" + entry.getValue());
		return Joiner.on("&").join(dataParts);
	}
	//gets registration ID from GCM so push notifications can be sent
	public void getRegIdIfNotRegistered(final boolean checkRegistered)
	{
		//must use AsyncTask since going on internet may lag app & should be done in bg
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			@WorkerThread
			protected Void doInBackground(Void... params)
			{
				if (!checkRegistered || !Settings.SINGLETON.getRegisteredInDatabase())
				{
					if (isDisconnected())
					{
						Log.i("Internet", "Can't get regID because internet is not on");
						return null;
					}

					try
					{
						InstanceID instanceID = InstanceID.getInstance(context);
						String token = instanceID.getToken(PROJECT_NUM_FOR_GCM, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
						sendRegIDToDatabase(token);
						Settings.SINGLETON.setRegID(token);
					}
					catch (IOException e)
					{
						Log.i("Internet", "GCM error: " + e.getMessage());
					}
				}
				return null;
			}
		}.execute(null, null, null);
	}
	//sends the ID to MySQL server so MySQL knows who to send the info to
	private void sendRegIDToDatabase(String regID)
	{
		String postData = PHP_REG_ID_KEY + "=" + regID;
		postToUrlWithData(PHP_REG_ID_URL, postData, new Callback()
		{
			@Override
			public void execute(String data)
			{
				//save so we don't send regID every time you open the app
				if (!data.isEmpty())
					Settings.SINGLETON.setRegisteredInDatabase(true);
			}
		});
	}
	public void sendNameEmailFeedbackToDatabase(String name, String email, String feedback)
	{
		String postData = PHP_FEEDBACK_NAME_KEY + "=" + name + "&" + PHP_FEEDBACK_EMAIL_KEY + "=" + email + "&" + PHP_FEEDBACK_FEEDBACK_KEY + "=" + feedback;
		postToUrlWithData(PHP_FEEDBACK_URL, postData, null);
	}
	public void sendTeacherFirstLastNameToDatabase(String firstName, String lastName)
	{
		String postData = PHP_REQUEST_TEACHER_FIRST_NAME_KEY + "=" + firstName +
				"&" + PHP_REQUEST_TEACHER_LAST_NAME_KEY + "=" + lastName +
				"&" + PHP_REG_ID_KEY + "=" + Settings.SINGLETON.getRegID();
		postToUrlWithData(PHP_REQUEST_TEACHER_URL, postData, null);
	}
	//specifically opens a JSON from the URL, checks internet connection, and posts EventBus events
	private void queryJSONFromURLWithData(final String urlString, @NonNull final String postData, @NonNull final EventBoolean booleanEvent)
	{
		if (isDisconnected()) {
			booleanEvent.setSuccessful(false);
			MyEventBus.eventBus.post(booleanEvent);
			return;
		}

		postToUrlWithData(urlString, postData, new Callback()
		{
			@Override
			public void execute(String data)
			{
				//empty data = event unsuccessful
				if (data.isEmpty()) {
					booleanEvent.setSuccessful(false);
					MyEventBus.eventBus.post(booleanEvent);
					return;
				}

				try
				{
					JSONObject jsonObject = new JSONObject(data);
					performActionWithJSONAndNotify(jsonObject, false);
					Log.i("json", jsonObject.toString());

					booleanEvent.setSuccessful(true);
					MyEventBus.eventBus.post(booleanEvent);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Log.wtf("Internet", "PHP GET failed");
				}
			}
		});
	}
	//passing "" for postData means nothing should be posted; passing null for callback means I don't need the info
	private void postToUrlWithData(final String urlString, @NonNull final String postData, @Nullable final Callback callback)
	{
		//must use AsyncTask since going on internet may lag app & should be done in bg
		new AsyncTask<Void, Void, String>() {
			@Override
			@WorkerThread
			protected String doInBackground(Void... params)
			{
				try
				{
					URL url = new URL(urlString);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setFixedLengthStreamingMode(postData.length());
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
					connection.getOutputStream().write(postData.getBytes("UTF-8"));
					String body = CharStreams.toString(new InputStreamReader(connection.getInputStream(), "UTF-8"));
					connection.disconnect();
					Log.i("Internet", "POST to MySQL succeeded");
					return body;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Log.wtf("Internet", "POST to MySQL failed :(");
				}
				return "";
			}

			@Override
			protected void onPostExecute(String body)
			{
				if (callback != null)
					callback.execute(body);
			}
		}.execute(null, null, null);
	}
}
