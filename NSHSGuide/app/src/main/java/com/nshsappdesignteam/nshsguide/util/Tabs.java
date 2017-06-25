package com.nshsappdesignteam.nshsguide.util;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.Log;

import com.nshsappdesignteam.nshsguide.R;

public final class Tabs
{
	//strings for Settings to save default landing page. The strings are arbitrary, but DO NOT change between builds
	//NOTE: these are NOT the actual names of the tabs (actual names will be localized & can change)
	public final String SAVED_ABSENT_TEACHERS;
	public final String SAVED_BLOCKS;
	public static Tabs SINGLETON;

	private Tabs(Context context)
	{
		String[] landingPages = context.getResources().getStringArray(R.array.values_default_landing_page);
		SAVED_ABSENT_TEACHERS = landingPages[0];
		SAVED_BLOCKS = landingPages[1];
	}
	public static void createSingleton(Context context)
	{
		SINGLETON = new Tabs(context);
	}
	@IdRes
	public int tabIdFromSavedLandingPage(String savedLandingPage)
	{
		if (savedLandingPage.equals(SAVED_ABSENT_TEACHERS))
			return R.id.tabAbsentTeachers;
		else if (savedLandingPage.equals(SAVED_BLOCKS))
			return R.id.tabBlocks;

		Log.wtf("Tabs", "tabIdFromSavedLandingPage failed, new savedLandingPage wasn't added to if/else statement");
		return 0;
	}
}
