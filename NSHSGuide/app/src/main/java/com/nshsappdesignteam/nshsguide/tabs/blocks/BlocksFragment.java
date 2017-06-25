package com.nshsappdesignteam.nshsguide.tabs.blocks;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nshsappdesignteam.nshsguide.MainActivity;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.ScheduleType;
import com.nshsappdesignteam.nshsguide.helper.clean.CleanFragment;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.InfiniteRecyclerScrollListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.InfoOfType;
import com.nshsappdesignteam.nshsguide.tabs.yourTeachers.TeacherSingleBlock;
import com.nshsappdesignteam.nshsguide.util.Date;
import com.nshsappdesignteam.nshsguide.util.Internet;
import com.nshsappdesignteam.nshsguide.util.SchoolYear;
import com.nshsappdesignteam.nshsguide.util.Settings;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BlocksFragment extends Fragment implements CleanFragment, OnDateSelectedListener
{
	private MaterialCalendarView blockMonthView;
	private RecyclerView blockDayRecycler;
	private BlockDayRecyclerAdapter blockDayAdapter;
	private LocalDate nextDate = LocalDate.now();

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_blocks, container, false);
		((MainActivity) getActivity()).setToolbarToBlockMonthToolbar();
		attachViewsToVars(view);
		setListeners();
		setUpRecycler();
		populateRecycler();
		setUpBlockMonth();
		sync();
		return view;
	}

	@Override
	public void attachViewsToVars(View view)
	{
		blockDayRecycler = (RecyclerView) view.findViewById(R.id.blockDayRecycler);
		blockMonthView = (MaterialCalendarView) getActivity().findViewById(R.id.blockMonthView);
	}
	@Override
	public void setListeners() {}
	@Override
	public void setUpRecycler()
	{
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		blockDayRecycler.setLayoutManager(layoutManager);
		blockDayAdapter = new BlockDayRecyclerAdapter();
		blockDayRecycler.setAdapter(blockDayAdapter);
		blockDayRecycler.addOnScrollListener(new InfiniteRecyclerScrollListener(layoutManager)
		{
			@Override
			public void onLoadMore(int page, int totalItemsCount)
			{
				populateRecyclerWithNextDay();
			}
		});
	}
	@Override
	public void populateRecycler()
	{
		//populate a week to start with
		for (int i = 0; i < 7; i++)
			populateRecyclerWithNextDay();
		jumpToRelevantBlock();
	}
	private void populateRecyclerWithNextDay()
	{
		if (Date.isBeforeDate(SchoolYear.end(), nextDate))
			return;

		Set<String> yourAbsentTeachersBlocks = getYourAbsentTeachersBlocks();
		List<Block> blocks = TeachersManager.SINGLETON.getBlocksWithLunches(nextDate);
		List<HasType<BlocksListType>> listItems = new ArrayList<>(blocks.size() + 1);

		listItems.add(new InfoOfType<>(nextDate.toString("MM/dd EEEE"), getSubheaderType())); //add subheader
		for (Block block : blocks)
			listItems.add(getTeacherWithBlock(block, yourAbsentTeachersBlocks));
		blockDayAdapter.addItems(listItems);
		nextDate = nextDate.plusDays(1);
	}
	private void jumpToRelevantBlock()  //this assumes the recycler is showing today's blocks
	{
		List<Block> blocks = TeachersManager.SINGLETON.getBlocksWithLunches(LocalDate.now());
		LocalTime time = LocalTime.now();
		int timeInMinutes = time.getHourOfDay() * 60 + time.getMinuteOfHour();

		int relevantIndex = 1;
		for (Block block : blocks)
		{
			if (timeInMinutes < block.endTime.toMinutes())
				break;
			relevantIndex++;
		}
		blockDayRecycler.scrollToPosition(relevantIndex);
	}
	private Set<String> getYourAbsentTeachersBlocks()
	{
		if (Date.sameAsToday(nextDate))
			return Settings.SINGLETON.getYourAbsentTeachersBlocks();
		return Collections.emptySet();
	}
	private BlocksListType getSubheaderType()
	{
		if (Settings.SINGLETON.getScheduleTypeForDay(nextDate) == ScheduleType.Special)
			return BlocksListType.SubheaderWithStar;
		else
			return BlocksListType.Subheader;
	}
	private TeacherWithBlock getTeacherWithBlock(Block block, Set<String> yourAbsentTeachers)
	{
		Pair<String, String> nameAndRoomNum = TeachersManager.SINGLETON.teacherTextAndRoomNumForBlock(block, yourAbsentTeachers);
		TeacherSingleBlock.Builder builder = new TeacherSingleBlock.Builder();
		builder.name = nameAndRoomNum.first;
		builder.blockLetter = block.letter;
		builder.blockNum = block.num;
		builder.roomNum = nameAndRoomNum.second;
		return new TeacherWithBlock(builder.build(), block);
	}

	//block month
	private void setUpBlockMonth()
	{
		blockMonthView.setSelectedDate(LocalDate.now().toDate());
		blockMonthView.setMinimumDate(SchoolYear.start().toDate());
		blockMonthView.setMaximumDate(SchoolYear.end().toDate());
		blockMonthView.setArrowColor(Color.WHITE);
		blockMonthView.setDateTextAppearance(R.layout.list_block_month);
		blockMonthView.setOnDateChangedListener(this);

		DayViewDecorator specialScheduleDecorator = new DayViewDecorator()
		{
			@Override
			public boolean shouldDecorate(CalendarDay day)
			{
				LocalDate date = LocalDate.fromDateFields(day.getDate());
				return Settings.SINGLETON.getScheduleTypeForDay(date) == ScheduleType.Special;
			}
			@Override
			public void decorate(DayViewFacade view)
			{
				Drawable bg = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_48dp, null);
				view.setBackgroundDrawable(bg);
			}
		};
		DayViewDecorator noSchoolDecorator = new DayViewDecorator()
		{
			@Override
			public boolean shouldDecorate(CalendarDay day)
			{
				LocalDate date = LocalDate.fromDateFields(day.getDate());
				return Settings.SINGLETON.getScheduleTypeForDay(date) == ScheduleType.NoSchool;
			}
			@Override
			public void decorate(DayViewFacade view)
			{
				Drawable bg = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_block_48dp, null);
				view.setBackgroundDrawable(bg);
			}
		};
		blockMonthView.addDecorators(specialScheduleDecorator, noSchoolDecorator);
	}
	@Override
	public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected)
	{
		blockDayAdapter.removeAll();

		nextDate = LocalDate.fromDateFields(date.getDate());
		for (int i = 0; i < 7; i++)
			populateRecyclerWithNextDay();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		((MainActivity) getActivity()).setToolbarToNormal();
	}
	private void sync()
	{
		Internet.SINGLETON.querySpecialSchedules();
	}
}
