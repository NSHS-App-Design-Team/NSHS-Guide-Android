package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.common.eventbus.Subscribe;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.async.EventGotAbsentTeachers;
import com.nshsappdesignteam.nshsguide.async.EventGotAnnouncement;
import com.nshsappdesignteam.nshsguide.async.EventYourTeachersChanged;
import com.nshsappdesignteam.nshsguide.async.MyEventBus;
import com.nshsappdesignteam.nshsguide.helper.clean.CleanFragment;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.InfoOfType;
import com.nshsappdesignteam.nshsguide.tabs.addTeacher.AddTeacherActivity;
import com.nshsappdesignteam.nshsguide.util.Internet;
import com.nshsappdesignteam.nshsguide.util.Settings;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AbsentTeachersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, CleanFragment
{
	private FloatingActionButton fab;
	//for swipe down to refresh
	private SwipeRefreshLayout swipeLayout;
	private RecyclerView teacherRecycler;
	private AbsentTeachersRecyclerAdapter adapter;
	private RelativeLayout emptyStateLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_absent_teachers, container, false);

		getActivity().setTitle(R.string.tab_absent_teachers);
		attachViewsToVars(view);
		setListeners();
		setUpRecycler();
		populateRecycler();
		//registers this class as a listener so completeRefreshing can be potentially called by Internet
		MyEventBus.eventBus.register(this);
		sync();

		return view;
	}

	public void attachViewsToVars(View view)
	{
		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
		swipeLayout.setColorSchemeResources(R.color.accent, R.color.colorPrimary);  //give the rotating refresh arrow its colors
		teacherRecycler = (RecyclerView) view.findViewById(R.id.absentTeacherRecycler);
		emptyStateLayout = (RelativeLayout) view.findViewById(R.id.emptyStateLayout);
		fab = (FloatingActionButton) view.findViewById(R.id.fab);
	}
	public void setListeners()
	{
		swipeLayout.setOnRefreshListener(this);
		fab.setOnClickListener(this);
	}
	public void setUpRecycler()
	{
		adapter = new AbsentTeachersRecyclerAdapter(getActivity());
		teacherRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		teacherRecycler.setAdapter(adapter);
	}
	public void populateRecycler()
	{
		Pair<Set<TeacherOtherAbsent>, Set<TeacherYourAbsent>> absentTeachers = TeachersManager.SINGLETON.absentTeachers();
		String savedAnnouncement = Settings.SINGLETON.getAnnouncement();

		if (savedAnnouncement == null && absentTeachers.first.isEmpty() && absentTeachers.second.isEmpty()) {
			showEmptyState(true);
			return;
		}

		//the list containing all the info our adapter needs, including subheaders
		List<HasType<AbsentTeacherListType>> listForAdapter = new ArrayList<>();
		if (savedAnnouncement != null)
		{
			listForAdapter.add(new InfoOfType<>(getString(R.string.subheader_announcement), AbsentTeacherListType.Subheader));
			listForAdapter.add(new InfoOfType<>(savedAnnouncement, AbsentTeacherListType.Announcement));
		}
		if (!absentTeachers.first.isEmpty() || !absentTeachers.second.isEmpty())
		{
			//repackage into a list for sorting
			List<TeacherOtherAbsent> otherTeachers = new ArrayList<>(absentTeachers.first);
			List<TeacherYourAbsent> yourTeachers = new ArrayList<>(absentTeachers.second);
			Collections.sort(otherTeachers);
			Collections.sort(yourTeachers);

			listForAdapter.add(new InfoOfType<>(getString(R.string.subheader_your_teachers), AbsentTeacherListType.Subheader));
			listForAdapter.addAll(yourTeachers);
			listForAdapter.add(new InfoOfType<>(getString(R.string.subheader_other_teachers), AbsentTeacherListType.Subheader));
			listForAdapter.addAll(otherTeachers);
		}
		adapter.setListInfo(listForAdapter);
		showEmptyState(false);
	}
	private void showEmptyState(boolean showEmptyState)
	{
		emptyStateLayout.setVisibility(showEmptyState ? View.VISIBLE : View.GONE);
	}
	private void sync()
	{
		if (Settings.SINGLETON.getSyncOn())
			onRefresh();
	}
	@Override
	public void onRefresh()
	{
		//try getting the info for the list
		Internet.SINGLETON.queryAbsentTeachers();
		Internet.SINGLETON.queryAnnouncement();
	}
	@Subscribe
	public void completeRefresh(EventGotAbsentTeachers eventGotAbsentTeachers)
	{
		//called by Internet.queryAbsentTeachers when it is done
		if (eventGotAbsentTeachers.successful())
		{
			TeachersManager.SINGLETON.saveNewYourAbsentTeachers();
			populateRecycler();
		}
		swipeLayout.setRefreshing(false);
	}
	@Subscribe
	public void completeRefresh(EventGotAnnouncement eventGotAnnouncement)
	{
		if (eventGotAnnouncement.successful())
			populateRecycler();
		swipeLayout.setRefreshing(false);
	}
	@Subscribe
	public void yourTeachersChanged(EventYourTeachersChanged eventYourTeachersChanged)
	{
		populateRecycler();
	}
	@Override
	public void onPause()
	{
		swipeLayout.setRefreshing(false);
		super.onPause();
	}
	@Override
	public void onDestroy()
	{
		swipeLayout.setRefreshing(false);
		MyEventBus.eventBus.unregister(this);
		super.onDestroy();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		fab.show();
	}
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.fab:
				fab.hide();
				Intent intent = new Intent(getActivity(), AddTeacherActivity.class);
				startActivity(intent);
				break;
		}
	}
}
