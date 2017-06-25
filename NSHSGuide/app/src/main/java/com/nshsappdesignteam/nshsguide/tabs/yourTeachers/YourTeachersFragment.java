package com.nshsappdesignteam.nshsguide.tabs.yourTeachers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.common.eventbus.Subscribe;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.async.EventYourTeachers;
import com.nshsappdesignteam.nshsguide.async.EventYourTeachersChanged;
import com.nshsappdesignteam.nshsguide.async.MyEventBus;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.clean.CleanFragment;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.LongClickDeleteFragment;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHClickListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHClickable;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHLongClickable;
import com.nshsappdesignteam.nshsguide.tabs.addTeacher.AddTeacherActivity;
import com.nshsappdesignteam.nshsguide.util.Settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YourTeachersFragment extends LongClickDeleteFragment implements View.OnClickListener, OnVHClickListener, CleanFragment
{
	private RecyclerView yourTeachersRecycler;
	private YourTeachersRecyclerAdapter adapter;
	private RelativeLayout emptyStateLayout;
	private FloatingActionButton fab;
	private final Set<Integer> selectedTeacherPositions = new HashSet<>();

	//for AddTeacherActivity, which will receive whatever teacher was pressed to edit on
	public static final String EXTRAS_TEACHER_KEY = "teacherWithInfo";

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_your_teachers, container, false);

		getActivity().setTitle(R.string.tab_your_teachers);
		attachViewsToVars(view);
		setUpRecycler();
		setListeners();
		populateRecycler();
		//registers this class as a listener so populateList & yourTeachersChanged can be called by event bus
		MyEventBus.eventBus.register(this);

		return view;
	}
	public void attachViewsToVars(View view)
	{
		yourTeachersRecycler = (RecyclerView) view.findViewById(R.id.yourTeachersRecycler);
		emptyStateLayout = (RelativeLayout) view.findViewById(R.id.emptyStateLayout);
		fab = (FloatingActionButton) view.findViewById(R.id.fab);
	}
	public void setListeners()
	{
		fab.setOnClickListener(this);
	}
	public void setUpRecycler()
	{
		adapter = new YourTeachersRecyclerAdapter(this);
		yourTeachersRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		yourTeachersRecycler.setAdapter(adapter);
	}
	public void populateRecycler()
	{
		//wrap getTeachers() in AsyncTask to prevent lag
		new AsyncTask<Void, Void, List<TeacherYours>>()
		{
			@Override
			@WorkerThread
			protected List<TeacherYours> doInBackground(Void... params)
			{
				return getTeachers();
			}
			@Override
			protected void onPostExecute(List<TeacherYours> teachers)
			{
				MyEventBus.eventBus.post(new EventYourTeachers(teachers));
			}
		}.execute(null, null, null);
	}
	private List<TeacherYours> getTeachers()
	{
		List<TeacherYours> teachers = new ArrayList<>();
		Set<TeacherSingleBlock> teacherForBlockNum = new HashSet<>(4);
		Set<Integer> groupedBlockNums = new HashSet<>(4);
		TeacherYours.Builder teacherBuilder = new TeacherYours.Builder();

		for (String blockLetter : Block.BLOCK_LETTERS)
		{
			//store the teachers into teacherForBlockNum first
			for (int blockNum : Block.BLOCK_NUMS)
			{
				TeacherSingleBlock teacherForBlock = Settings.SINGLETON.getTeacherForBlock(blockLetter, blockNum);
				if (teacherForBlock != null)
					teacherForBlockNum.add(teacherForBlock);
			}

			//group teachers of same block but different block num
			for (TeacherSingleBlock teacher : teacherForBlockNum)
			{
				if (groupedBlockNums.contains(teacher.blockNum))
					continue;

				for (TeacherSingleBlock teacherToCheck : teacherForBlockNum)
				{
					if (groupedBlockNums.contains(teacherToCheck.blockNum))
						continue;
					if (!teacher.getName().equals(teacherToCheck.getName()))
						continue;
					if (!teacher.subject.equals(teacherToCheck.subject))
						continue;
					if (!teacher.roomNum.equals(teacherToCheck.roomNum))
						continue;

					groupedBlockNums.add(teacherToCheck.blockNum);
					teacherBuilder.blockNums.add(teacherToCheck.blockNum);
				}

				teacherBuilder.lunch = getLunchFromTaughtBlocks(blockLetter, teacherBuilder.blockNums);
				teacherBuilder.name = teacher.getName();
				teacherBuilder.blockLetter = blockLetter;
				teacherBuilder.subject = teacher.subject;
				teacherBuilder.roomNum = teacher.roomNum;
				teachers.add(teacherBuilder.build());

				teacherBuilder.blockNums.clear();   //allow next teacher's blocks to be stored in the same builder
			}

			groupedBlockNums.clear();
			teacherForBlockNum.clear();
		}
		return teachers;
	}
	private int getLunchFromTaughtBlocks(String blockLetter, Set<Integer> blockNums)
	{
		for (int blockNum : blockNums)
			if (Block.LUNCH_BLOCK_GIVEN_DAY.containsValue(blockLetter + blockNum))
				return Settings.SINGLETON.getLunchNumForBlockLetter(blockLetter);

		return 0;
	}
	private void toggleEmptyState()
	{
		emptyStateLayout.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE);
	}
	//called by eventBus from AsyncTask in populateRecycler()
	@Subscribe
	public void populateRecycler(EventYourTeachers event)
	{
		adapter.setTeachers(event.teachers);
		toggleEmptyState();
	}
	@Subscribe
	public void yourTeachersChanged(EventYourTeachersChanged eventYourTeachersChanged)
	{
		populateRecycler();
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
	@Override
	public void onDestroy()
	{
		MyEventBus.eventBus.unregister(this);
		super.onDestroy();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		fab.show();
	}

	/*
	METHODS TO BE CALLED BY ADAPTER
	 */
	@Override
	public void onItemClick(int position, VHClickable vhClickable)
	{
		if (actionModeShowing)
		{
			if (selectedTeacherPositions.contains(position))
				selectedTeacherPositions.remove(Integer.valueOf(position)); //must wrap int into Integer, since remove is overloaded
			else
				selectedTeacherPositions.add(position);
			VHLongClickable viewHolder = (VHLongClickable) yourTeachersRecycler.findViewHolderForAdapterPosition(position);
			viewHolder.setSelected(!viewHolder.isSelected());
		}
		else
		{
			ListYourTeacherNoSubject viewHolder = (ListYourTeacherNoSubject) vhClickable;
			TeacherYours teacher = adapter.getTeacherAtPosition(position);
			startAddTeachersActivity(teacher, viewHolder);
		}
	}
	private void startAddTeachersActivity(TeacherYours teacher, ListYourTeacherNoSubject viewHolder)
	{
		Intent intent = new Intent(getActivity(), AddTeacherActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(EXTRAS_TEACHER_KEY, teacher);
		Pair<View, String> blockImagePair = new Pair<View, String>(viewHolder.blockImage, getString(R.string.transition_target_block_image));
		Pair<View, String> block1ImagePair = new Pair<View, String>(viewHolder.blockNumImageForNum.get(1), getString(R.string.transition_target_block_1_image));
		Pair<View, String> block2ImagePair = new Pair<View, String>(viewHolder.blockNumImageForNum.get(2), getString(R.string.transition_target_block_2_image));
		Pair<View, String> block3ImagePair = new Pair<View, String>(viewHolder.blockNumImageForNum.get(3), getString(R.string.transition_target_block_3_image));
		Pair<View, String> block4ImagePair = new Pair<View, String>(viewHolder.blockNumImageForNum.get(4), getString(R.string.transition_target_block_4_image));
		startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
				blockImagePair, block1ImagePair, block2ImagePair, block3ImagePair, block4ImagePair).toBundle());
	}
	@Override
	protected void onDeleteClick()
	{
		Set<TeacherYours> teachersToDelete = new HashSet<>(selectedTeacherPositions.size());
		for (int position : selectedTeacherPositions)
		{
			deselectForPosition(position);
			teachersToDelete.add(adapter.getTeacherAtPosition(position));
		}
		//delete using teachers, not positions, because positions will change as List resizes
		for (TeacherYours teacher : teachersToDelete)
			adapter.removeTeacher(teacher);

		selectedTeacherPositions.clear();
		toggleEmptyState();
	}
	@Override
	protected void deselectAll()
	{
		for (int position : selectedTeacherPositions)
			deselectForPosition(position);
		selectedTeacherPositions.clear();
	}
	private void deselectForPosition(int position)
	{
		VHLongClickable vh = (VHLongClickable) yourTeachersRecycler.findViewHolderForAdapterPosition(position);
		if (vh != null)
			vh.setSelected(false);
	}
}
