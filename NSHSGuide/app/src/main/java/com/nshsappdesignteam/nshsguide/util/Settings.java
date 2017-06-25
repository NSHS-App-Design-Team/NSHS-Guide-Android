package com.nshsappdesignteam.nshsguide.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

import com.nshsappdesignteam.nshsguide.BuildConfig;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.BlockSpecial;
import com.nshsappdesignteam.nshsguide.helper.ScheduleType;
import com.nshsappdesignteam.nshsguide.tabs.absentTeachers.TeacherOtherAbsent;
import com.nshsappdesignteam.nshsguide.tabs.absentTeachers.TeacherYourAbsent;
import com.nshsappdesignteam.nshsguide.tabs.yourTeachers.TeacherSingleBlock;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public final class Settings
{
	//keys. Comments show what info would be stored in each
	private final String REGISTERED_IN_DATABASE; //boolean
	private final String REG_ID;    //string
	private final String ABSENT_TEACHERS;  //"Chu, David|10101010|some info", "Lin, Eric|11111111|other info"
	private final String ABSENT_TEACHERS_UPDATE_DATE;   //"7|25"    //month|day
	private final String YOUR_ABSENT_TEACHERS; //"A1", "D2", "F4"
	private final String ANNOUNCEMENT;   //"123456|some announcement"
	private final String BLOCK_NOTIFICATIONS_ON;    //boolean
	private final String GENERAL_NOTIFICATIONS_ON;    //boolean
	private final String NOTIFICATION_VIBRATE_ON;  //boolean
	private final String NOTIFICATION_LIGHT_ON;    //boolean
	private final String BLOCK_NOTIFICATIONS_TIME_ADJUST;   //15
	private final String DEFAULT_LANDING_PAGE;    //Tabs.SAVED_ABSENT_TEACHERS
	private final String NOTIFICATIONS_SET; //boolean
	private final String DEVELOPER_MODE_ON; //boolean
	private final String SPECIAL_SCHEDULE;   //ordered BlockSpecial toString()'s separated by \n
	private final String SPECIAL_SCHEDULE_DATES;    //"2016-06-01, "2016-06-15"
	private final String FIRST_RUN; //boolean
	private final String TEACHERS_LIST; //"Chu, David", "Lin, Eric", ... (all teachers at NSHS)
	private final String SYNC_ON; //boolean
	private final String WAS_DAYLIGHT_SAVINGS_ON_LAST_CHECK;    //boolean
	private final String CURRENT_VERSION;  //"1.4.1"
	private final String NAME;
	private final String EMAIL;

	private final SharedPreferences preferences;
	public static Settings SINGLETON;

	private Settings(Context context)
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		REGISTERED_IN_DATABASE = context.getString(R.string.key_registered_in_database);
		REG_ID = context.getString(R.string.key_reg_id);
		ABSENT_TEACHERS = context.getString(R.string.key_absent_teachers);
		ABSENT_TEACHERS_UPDATE_DATE = context.getString(R.string.key_absent_teachers_update_date);
		YOUR_ABSENT_TEACHERS = context.getString(R.string.key_your_absent_teachers);
		ANNOUNCEMENT = context.getString(R.string.key_announcement);
		BLOCK_NOTIFICATIONS_ON = context.getString(R.string.key_block_notifications_on);
		GENERAL_NOTIFICATIONS_ON = context.getString(R.string.key_general_notifications_on);
		NOTIFICATION_VIBRATE_ON = context.getString(R.string.key_notification_vibrate_on);
		NOTIFICATION_LIGHT_ON = context.getString(R.string.key_notification_light_on);
		BLOCK_NOTIFICATIONS_TIME_ADJUST = context.getString(R.string.key_block_notifications_time_adjust);
		DEFAULT_LANDING_PAGE = context.getString(R.string.key_default_landing_page);
		NOTIFICATIONS_SET = context.getString(R.string.key_notifications_set);
		DEVELOPER_MODE_ON = context.getString(R.string.key_developer_mode_on);
		SPECIAL_SCHEDULE = context.getString(R.string.key_special_schedule);
		SPECIAL_SCHEDULE_DATES = context.getString(R.string.key_special_schedule_dates);
		FIRST_RUN = context.getString(R.string.key_first_run);
		TEACHERS_LIST = context.getString(R.string.key_teachers_list);
		SYNC_ON = context.getString(R.string.key_sync_on);
		WAS_DAYLIGHT_SAVINGS_ON_LAST_CHECK = context.getString(R.string.key_was_daylight_savings_on_last_check);
		CURRENT_VERSION = context.getString(R.string.key_current_version);
		NAME = context.getString(R.string.key_name);
		EMAIL = context.getString(R.string.key_email);
	}
	public static void createSingleton(Context context)
	{
		SINGLETON = new Settings(context);
	}

	/*
	ABSENT TEACHERS
	 */
	public Set<TeacherOtherAbsent> getAbsentTeachersForToday()
	{
		if (noAbsencesToday())
			return Collections.emptySet();

		//convert from set of strings to list of TeacherOtherAbsent
		Set<String> absentTeachersAsStrings =  preferences.getStringSet(ABSENT_TEACHERS, Collections.<String>emptySet());
		Set<TeacherOtherAbsent> allAbsentTeachers = new HashSet<>(absentTeachersAsStrings.size());
		for (String absentTeacherString : absentTeachersAsStrings)
			allAbsentTeachers.add(TeacherOtherAbsent.fromString(absentTeacherString));

		return allAbsentTeachers;
	}
	public boolean noAbsencesToday()
	{
		String absentTeachersUpdateDate = preferences.getString(ABSENT_TEACHERS_UPDATE_DATE, null);
		if (absentTeachersUpdateDate == null)
			return true;

		String[] absentTeachersUpdateDateSplit = absentTeachersUpdateDate.split("\\|", -1);
		int updateMonth = Integer.parseInt(absentTeachersUpdateDateSplit[0]);
		int updateDay = Integer.parseInt(absentTeachersUpdateDateSplit[1]);
		LocalDate today = LocalDate.now();

		boolean noAbsencesToday = today.getMonthOfYear() != updateMonth || today.getDayOfMonth() != updateDay;
		//if the absences aren't from today, they're old absences and will never be used. Delete them so this method runs faster next time
		if (noAbsencesToday)
			deleteAbsentTeachers();
		return noAbsencesToday;
	}
	private void deleteAbsentTeachers()
	{
		remove(ABSENT_TEACHERS);
		remove(ABSENT_TEACHERS_UPDATE_DATE);
		remove(YOUR_ABSENT_TEACHERS);
	}
	public void setAbsentTeachers(JSONObject jsonObject)
	{
		String date = jsonObject.optString("date");
		setStringNow(ABSENT_TEACHERS_UPDATE_DATE, date);
		setStringSetNow(ABSENT_TEACHERS, orderedStringSetFromJSON(jsonObject, false));
	}

	/*
	YOUR ABSENT TEACHERS
	 */
	public Set<String> getYourAbsentTeachersBlocks()
	{
		if (noAbsencesToday())
			return Collections.emptySet();
		return preferences.getStringSet(YOUR_ABSENT_TEACHERS, Collections.<String>emptySet());
	}
	public void setYourAbsentTeachers(Set<TeacherYourAbsent> yourAbsentTeachers)
	{
		Set<String> absentBlocks = new HashSet<>(yourAbsentTeachers.size());
		for (TeacherYourAbsent yourAbsentTeacher : yourAbsentTeachers)
			absentBlocks.add(yourAbsentTeacher.block.getBlock());
		setStringSet(YOUR_ABSENT_TEACHERS, absentBlocks);
	}

	/*
	ANNOUNCEMENT
	 */
	@Nullable
	public String getAnnouncement()
	{
		String announcementWithTime = preferences.getString(ANNOUNCEMENT, null);
		if (announcementWithTime != null)
			if (timeCorrectForShowingAnnouncement(announcementWithTime))
				return announcementWithTime.split("\\|", -1)[1];

		return null;
	}
	private boolean timeCorrectForShowingAnnouncement(String announcementWithTime)
	{
		String[] announcementInParts = announcementWithTime.split("\\|", -1);
		int endEpoch = Integer.parseInt(announcementInParts[0]);
		long todayInEpoch = System.currentTimeMillis() / 1000;

		boolean timeCorrect = todayInEpoch <= endEpoch;
		//if the announcement isn't to be shown now, it will never be shown. Delete the announcement so it fails faster next time
		if (!timeCorrect)
			remove(ANNOUNCEMENT);
		return timeCorrect;
	}
	public void setAnnouncement(JSONObject jsonObject)
	{
		//convert ["endEpoch":"123456", "info":"something to announce"] to "123456|something to announce"
		String endEpoch = jsonObject.optString("endEpoch");
		String info = jsonObject.optString("info");
		if (endEpoch.isEmpty() || info.isEmpty())
			return;
		setStringNow(ANNOUNCEMENT, endEpoch + "|" + info);
	}

	/*
	SPECIAL SCHEDULE
	 */
	public List<Block> getSpecialScheduleForDay(LocalDate date)
	{
		String specialSchedule = preferences.getString(keyForDay(SPECIAL_SCHEDULE, date), "");
		if (specialSchedule.isEmpty())
			return Collections.emptyList();

		//split doesn't have -1 as the last argument as there WILL be an extra \n at the end of the string with nothing after it
		String[] blocksAsString = specialSchedule.split("\n");
		List<Block> blocks = new ArrayList<>(blocksAsString.length);

		for (String blockString : blocksAsString)
			insertBlockStringIntoList(blockString, blocks);
		return blocks;
	}
	public String getSpecialScheduleTextForDayText(String dayText)
	{
		//assumes dayText is in correct format as specified by Internet.SPECIAL_SCHEDULE_DATE_PATTERN
		return preferences.getString(SPECIAL_SCHEDULE + dayText, "");
	}
	private void insertBlockStringIntoList(String blockString, List<Block> blocks)
	{
		Block block = Block.fromString(blockString);
		blocks.add(block);
		saveCustomBlockNameAsTeacher(block);
	}
	private void saveCustomBlockNameAsTeacher(Block block)
	{
		if (!(block instanceof BlockSpecial))
			return;
		BlockSpecial specialBlock = (BlockSpecial) block;
		if (specialBlock.customName.isEmpty())
			return;
		if (!specialBlock.letter.equals("S"))   //only save custom name for special blocks to avoid unwanted overriding
			return;

		TeacherSingleBlock.Builder classAsTeacher = new TeacherSingleBlock.Builder();
		classAsTeacher.blockLetter = specialBlock.letter;
		classAsTeacher.blockNum = specialBlock.num;
		classAsTeacher.name = specialBlock.customName;
		classAsTeacher.subject = specialBlock.customName;
		classAsTeacher.roomNum = "";
		setTeacherForBlock(classAsTeacher.build());
	}
	public ScheduleType getScheduleTypeForDay(LocalDate date)
	{
		String schedule = preferences.getString(keyForDay(SPECIAL_SCHEDULE, date), null);
		if (schedule == null)
			return ScheduleType.Normal;
		if (schedule.isEmpty())
			return ScheduleType.NoSchool;
		else
			return ScheduleType.Special;
	}
	public void addAndRemoveSpecialSchedules(JSONObject jsonObject)
	{
		String datesToRemoveString = jsonObject.optString("datesToRemove");
		String datesToAddString = jsonObject.optString("datesToAdd");
		String[] datesToRemove = datesToRemoveString.split("\\|");
		String[] datesToAdd = datesToAddString.split("\\|");

		if (!datesToRemoveString.isEmpty())
			for (String date : datesToRemove)
				removeSpecialScheduleForDayText(date);
		if (!datesToAddString.isEmpty())
			for (String date : datesToAdd)
				setSpecialScheduleForDayText(jsonObject.optString(date), date);

		Set<String> specialScheduleDates = getSpecialScheduleDates();
		specialScheduleDates.removeAll(Arrays.asList(datesToRemove));
		if (!datesToAddString.isEmpty())
			specialScheduleDates.addAll(Arrays.asList(datesToAdd));
		setSpecialScheduleDates(specialScheduleDates);
	}
	private void removeSpecialScheduleForDayText(String dayText)
	{
		LocalDate date = LocalDate.parse(dayText, DateTimeFormat.forPattern(Internet.SPECIAL_SCHEDULE_DATE_PATTERN));
		Notifications.SINGLETON.destroySpecialScheduleBlocks(date);

		remove(SPECIAL_SCHEDULE + dayText);
	}
	private void setSpecialScheduleForDayText(String schedule, String dayText)
	{
		setStringNow(SPECIAL_SCHEDULE + dayText, schedule);

		if (!schedule.isEmpty())
		{
			LocalDate date = LocalDate.parse(dayText, DateTimeFormat.forPattern(Internet.SPECIAL_SCHEDULE_DATE_PATTERN));
			Notifications.SINGLETON.setSpecialScheduleBlocks(date);
		}
	}
	public Set<String> getSpecialScheduleDates()
	{
		return new HashSet<>(preferences.getStringSet(SPECIAL_SCHEDULE_DATES, Collections.<String>emptySet()));
	}
	public void setSpecialScheduleDates(Set<String> dates)
	{
		setStringSet(SPECIAL_SCHEDULE_DATES, dates);
	}

	/*
	SAVED TEACHERS & LUNCHES
	 */
	public void setTeacherForBlock(TeacherSingleBlock teacher)
	{
		//stored as such: "A1" returns "Chu, David|AP Comp Sci|4105", last 2 parts optional
		setString(teacher.blockLetter + teacher.blockNum, teacher.getName() + "|" + teacher.subject + "|" + teacher.roomNum);
	}
	@Nullable
	public TeacherSingleBlock getTeacherForBlock(Block block)
	{
		return getTeacherForBlock(block.letter, block.num);
	}
	@Nullable
	public TeacherSingleBlock getTeacherForBlock(String blockLetter, int blockNum)
	{
		String savedTeacher = preferences.getString(blockLetter + blockNum, null);
		if (savedTeacher == null)
			return null;

		String[] savedTeacherInParts = savedTeacher.split("\\|", -1);
		String teacherName;
		String subject = "";
		String roomNum = "";
		teacherName = savedTeacherInParts[0];
		if (savedTeacherInParts.length == 3)
		{
			subject = savedTeacherInParts[1];
			roomNum = savedTeacherInParts[2];
		}

		TeacherSingleBlock.Builder builder = new TeacherSingleBlock.Builder();
		builder.blockLetter = blockLetter;
		builder.blockNum = blockNum;
		builder.name = teacherName;
		builder.subject = subject;
		builder.roomNum = roomNum;
		return builder.build();
	}
	public void removeTeacherForBlock(String block)
	{
		remove(block);
	}
	public void setLunchNumForBlockLetter(int lunch, String blockLetter)
	{
		setInt(blockLetter + "Lunch", lunch);
	}
	public int getLunchNumForBlockLetter(String blockLetter)
	{
		//1st lunch by default
		return preferences.getInt(blockLetter + "Lunch", 1);
	}
	public void removeLunchNumForBlockLetter(String blockLetter)
	{
		remove(blockLetter + "Lunch");
	}

	/*
	USER-CONTROLLED SETTINGS (should change default values along with preferences.xml)
	 */
	public boolean getBlockNotificationsOn()
	{
		return preferences.getBoolean(BLOCK_NOTIFICATIONS_ON, true);
	}
	public boolean getGeneralNotificationsOn()
	{
		return preferences.getBoolean(GENERAL_NOTIFICATIONS_ON, true);
	}
	public boolean getNotificationVibrateOn()
	{
		return preferences.getBoolean(NOTIFICATION_VIBRATE_ON, false);
	}
	public boolean getNotificationLightOn()
	{
		return preferences.getBoolean(NOTIFICATION_LIGHT_ON, true);
	}
	public int getBlockNotificationsTimeAdjust()
	{
		return preferences.getInt(BLOCK_NOTIFICATIONS_TIME_ADJUST, 10);
	}
	public boolean getSyncOn()
	{
		return preferences.getBoolean(SYNC_ON, true);
	}

	/*
	OTHER SETTINGS
	 */
	public boolean getRegisteredInDatabase()
	{
		return preferences.getBoolean(REGISTERED_IN_DATABASE, false);
	}
	public void setRegisteredInDatabase(boolean registeredInDatabase)
	{
		setBool(REGISTERED_IN_DATABASE, registeredInDatabase);
	}
	public String getRegID()
	{
		return preferences.getString(REG_ID, "");
	}
	public void setRegID(String regID)
	{
		setStringNow(REG_ID, regID);
	}
	public boolean getNotificationsSet()
	{
		return preferences.getBoolean(NOTIFICATIONS_SET, false);
	}
	public void setNotificationsSet(boolean notificationsSet)
	{
		setBool(NOTIFICATIONS_SET, notificationsSet);
	}
	//saved as string but returned as R.id (can't store R.id because they're not constant between builds)
	@IdRes
	public int getDefaultLandingPage()
	{
		String savedLandingPage = preferences.getString(DEFAULT_LANDING_PAGE, Tabs.SINGLETON.SAVED_ABSENT_TEACHERS);
		return Tabs.SINGLETON.tabIdFromSavedLandingPage(savedLandingPage);
	}
	public boolean isDeveloperModeOn()
	{
		return preferences.getBoolean(DEVELOPER_MODE_ON, false);
	}
	public void setDeveloperModeOn()
	{
		setBool(DEVELOPER_MODE_ON, true);
	}
	public boolean isFirstRun()
	{
		return preferences.getBoolean(FIRST_RUN, true);
	}
	public void setFirstRunFalse()
	{
		setBool(FIRST_RUN, false);
	}
	public Set<String> getTeachersList()
	{
		Set<String> teachersSet = preferences.getStringSet(TEACHERS_LIST, null);
		if (teachersSet == null || teachersSet.isEmpty())
			return Collections.emptySet();

		return teachersSet;
	}
	public void setTeachersList(JSONObject jsonObject)
	{
		setStringSet(TEACHERS_LIST, orderedStringSetFromJSON(jsonObject, false));
	}
	private boolean isDaylightSavings()
	{
		TimeZone timeZone = TimeZone.getDefault();
		return timeZone.inDaylightTime(new Date());
	}
	public boolean daylightSavingChanged()
	{
		boolean isDaylightSavings = isDaylightSavings();
		boolean changed = preferences.getBoolean(WAS_DAYLIGHT_SAVINGS_ON_LAST_CHECK, isDaylightSavings) != isDaylightSavings;
		setBool(WAS_DAYLIGHT_SAVINGS_ON_LAST_CHECK, isDaylightSavings);
		return changed;
	}
	public boolean wasCurrentVersion()
	{
		return preferences.getString(CURRENT_VERSION, BuildConfig.VERSION_NAME).equals(BuildConfig.VERSION_NAME);
	}
	public void setCurrentVersion()
	{
		setString(CURRENT_VERSION, BuildConfig.VERSION_NAME);
	}
	public String getName()
	{
		return preferences.getString(NAME, "");
	}
	public void setName(String name)
	{
		setString(NAME, name);
	}
	public String getEmail()
	{
		return preferences.getString(EMAIL, "");
	}
	public void setEmail(String email)
	{
		setString(EMAIL, email);
	}

	/*
	GENERAL PRIVATE SET METHODS FPR REUSE
	 */
	private void setInt(String key, int value)
	{
		Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.apply();
	}
	private void setIntNow(String key, int value)
	{
		Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	private void setBool(String key, boolean value)
	{
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}
	private void setString(String key, String value)
	{
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.apply();
	}
	private void setStringNow(String key, String value)
	{
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	private void setStringSet(String key, Set<String> value)
	{
		Editor editor = preferences.edit();
		editor.putStringSet(key, value);
		editor.apply();
	}
	private void setStringSetNow(String key, Set<String> value)
	{
		Editor editor = preferences.edit();
		editor.putStringSet(key, value);
		editor.commit();
	}
	private void remove(String key)
	{
		Editor editor = preferences.edit();
		editor.remove(key);
		editor.apply();
	}
	private String keyForDay(String keyConstant, LocalDate date)
	{
		return keyConstant + date.toString(Internet.SPECIAL_SCHEDULE_DATE_PATTERN);
	}
	private Set<String> orderedStringSetFromJSON(JSONObject jsonObject, boolean ordered)
	{
		Set<String> set;
		if (ordered)
			set = new LinkedHashSet<>();
		else
			set = new HashSet<>();

		//iterates through JSON Map of index to String
		int index = 0;
		while (jsonObject.has(String.valueOf(index)))
		{
			String nextString = jsonObject.optString(String.valueOf(index));
			set.add(nextString);
			index++;
		}
		return set;
	}
}
