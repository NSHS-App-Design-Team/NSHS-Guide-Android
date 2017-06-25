package com.nshsappdesignteam.nshsguide.tabs.tutorial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TutorialPagerAdapter extends FragmentPagerAdapter
{
	private final int[] pagesLayoutIds;

	public TutorialPagerAdapter(FragmentManager fm, int[] pagesLayoutIds)
	{
		super(fm);
		this.pagesLayoutIds = pagesLayoutIds;
	}
	@Override
	public Fragment getItem(int position)
	{
		return TutorialFragment.newInstance(pagesLayoutIds[position]);
	}
	@Override
	public int getCount()
	{
		return pagesLayoutIds.length;
	}
}
