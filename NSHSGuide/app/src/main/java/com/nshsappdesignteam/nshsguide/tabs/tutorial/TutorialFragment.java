package com.nshsappdesignteam.nshsguide.tabs.tutorial;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialFragment extends Fragment
{
	private static final String NEW_INSTANCE_LAYOUT_ID_KEY = "layoutId";

	public static TutorialFragment newInstance(@LayoutRes int layoutId)
	{
		Bundle args = new Bundle();
		args.putInt(NEW_INSTANCE_LAYOUT_ID_KEY, layoutId);
		TutorialFragment fragment = new TutorialFragment();
		fragment.setArguments(args);
		return fragment;
	}
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		int layoutId = getArguments().getInt(NEW_INSTANCE_LAYOUT_ID_KEY);
		return inflater.inflate(layoutId, container, false);
	}
}
