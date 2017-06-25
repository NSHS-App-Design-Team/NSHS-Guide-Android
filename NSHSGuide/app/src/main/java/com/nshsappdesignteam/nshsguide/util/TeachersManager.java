package com.nshsappdesignteam.nshsguide.util;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.ScheduleType;
import com.nshsappdesignteam.nshsguide.tabs.absentTeachers.TeacherOtherAbsent;
import com.nshsappdesignteam.nshsguide.tabs.absentTeachers.TeacherYourAbsent;
import com.nshsappdesignteam.nshsguide.tabs.yourTeachers.TeacherSingleBlock;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TeachersManager
{
	public static TeachersManager SINGLETON;
	public final String FREE_BLOCK;
	public final String CANCELLED_CLASS;
	public final String ROOM;
	public final ImmutableList<String> LUNCHES;

	public static void createSingleton(Context context)
	{
		SINGLETON = new TeachersManager(context);
	}
	private TeachersManager(Context context)
	{
		FREE_BLOCK = context.getString(R.string.free_block);
		CANCELLED_CLASS = context.getString(R.string.cancelled_class);
		ROOM = context.getString(R.string.room);
		LUNCHES = ImmutableList.copyOf(context.getResources().getStringArray(R.array.lunches));
	}
	public List<Block> blocksForDay(LocalDate date)
	{
		if (Date.isWeekend(date))
			return Collections.emptyList();

		ScheduleType scheduleType = Settings.SINGLETON.getScheduleTypeForDay(date);
		if (scheduleType == ScheduleType.NoSchool)
			return Collections.emptyList();
		if (scheduleType == ScheduleType.Normal)
			return Block.BLOCKS_GIVEN_DAY.get(date.getDayOfWeek());

		return Settings.SINGLETON.getSpecialScheduleForDay(date);
	}
	public List<Block> getBlocksWithLunches(LocalDate date)
	{
		List<Block> blocks = blocksForDay(date);
		if (hasFreeOrCancelledLunch(blocks, date))
			blocks = insertAllLunchesIntoBlocks(blocks);
		else
			blocks = insertLunchesIntoBlocks(blocks);
		return blocks;
	}
	public boolean hasFreeOrCancelledLunch(List<Block> blocksForDay, LocalDate date)
	{
		for (Block block : blocksForDay)
		{
			if (!block.isLunchBlock)
				continue;
			if (Date.sameAsToday(date))
				return isFreeBlock(block) || isCancelledClass(block);
			else
				return isFreeBlock(block);
		}
		return false;
	}

	public List<Block> insertAllLunchesIntoBlocks(List<Block> blocksForDay)
	{
		if (blocksForDay instanceof ImmutableList)
			blocksForDay = new ArrayList<>(blocksForDay);

		for (int i = 0; i < blocksForDay.size(); i++)
		{
			Block block = blocksForDay.get(i);
			if (!block.isLunchBlock)
				continue;

			Block lunch1 = Block.createLunchAsBlock(1, block);
			Block lunch2 = Block.createLunchAsBlock(2, block);
			Block lunch3 = Block.createLunchAsBlock(3, block);
			blocksForDay.add(i+1, lunch1);
			blocksForDay.add(i+2, lunch2);
			blocksForDay.add(i+3, lunch3);
		}

		return blocksForDay;
	}

	public List<Block> insertLunchesIntoBlocks(List<Block> blocksForDay)
	{
		//requires a mutable list
		if (blocksForDay instanceof ImmutableList)
			blocksForDay = new ArrayList<>(blocksForDay);

		for (int i = 0; i < blocksForDay.size(); i++)
		{
			if (!blocksForDay.get(i).isLunchBlock)
				continue;

			Block block = blocksForDay.get(i);
			int lunchNum = Settings.SINGLETON.getLunchNumForBlockLetter(block.letter);
			Block lunch = Block.createLunchAsBlock(lunchNum, block);
			blocksForDay.remove(i);

			switch (lunchNum)
			{
				case 1:
					blocksForDay.add(i, lunch);
					blocksForDay.add(i + 1, new Block(block.letter, block.num, lunch.endTime, block.endTime, false));
					break;
				case 2:
					blocksForDay.add(i, new Block(block.letter, block.num, block.startTime, lunch.startTime, false));
					blocksForDay.add(i + 1, lunch);
					blocksForDay.add(i + 2, new Block(block.letter, block.num, lunch.endTime, block.endTime, false));
					break;
				case 3:
					blocksForDay.add(i, new Block(block.letter, block.num, block.startTime, lunch.startTime, false));
					blocksForDay.add(i + 1, lunch);
					break;
			}
			//exit the for loop as there's only one lunch a day
			return blocksForDay;
		}
		Log.i("TeachersManager", "insertLunchesIntoBlocks called but no lunch blocks were found");
		return blocksForDay;
	}
	public Pair<Set<TeacherOtherAbsent>, Set<TeacherYourAbsent>> absentTeachers()
	{
		Set<TeacherOtherAbsent> allAbsentTeachers = Settings.SINGLETON.getAbsentTeachersForToday();
		if (allAbsentTeachers.isEmpty())
			return new Pair<>(Collections.<TeacherOtherAbsent>emptySet(), Collections.<TeacherYourAbsent>emptySet());

		List<Block> blocksForDay = blocksForDay(LocalDate.now());
		if (blocksForDay.isEmpty())
			return new Pair<>(Collections.<TeacherOtherAbsent>emptySet(), Collections.<TeacherYourAbsent>emptySet());

		Set<TeacherOtherAbsent> otherAbsentTeachers = new HashSet<>(allAbsentTeachers);
		Set<TeacherYourAbsent> yourAbsentTeachers = new HashSet<>();

		for (Block block : blocksForDay)
		{
			TeacherSingleBlock teacher = Settings.SINGLETON.getTeacherForBlock(block.letter, block.num);
			if (teacher == null)
				continue;

			for (TeacherOtherAbsent absentTeacher : allAbsentTeachers)
			{
				if (!teacher.getName().equals(absentTeacher.getName()) || !absentTeacher.absentForBlock.get(block.letter))
					continue;

				//proven to be your teacher
				yourAbsentTeachers.add(new TeacherYourAbsent(teacher.getName(), absentTeacher.getInfo(), block));

				if (otherAbsentTeachers.contains(absentTeacher))
					otherAbsentTeachers.remove(absentTeacher);
			}
		}
		return new Pair<>(otherAbsentTeachers, yourAbsentTeachers);
	}
	/**
	 * Basically absentTeachers() but with the specified intent of saving the new list
	 * @return list of your absent teachers
	 */
	public Set<TeacherYourAbsent> saveNewYourAbsentTeachers()
	{
		Set<TeacherYourAbsent> yourAbsentTeachers = absentTeachers().second;
		Settings.SINGLETON.setYourAbsentTeachers(yourAbsentTeachers);
		return yourAbsentTeachers;
	}
	//yourAbsentTeachersBlocks provided in parameters to save on CPU (only retrieve Set once in a loop or a list)
	public Pair<String, String> teacherTextAndRoomNumForBlock(Block block, Set<String> yourAbsentTeachersBlocks)
	{
		TeacherSingleBlock teacher = Settings.SINGLETON.getTeacherForBlock(block);
		String roomNumText;
		if (teacher == null || teacher.roomNum.isEmpty())
			roomNumText = "";
		else
			roomNumText = ROOM + " " + teacher.roomNum;

		//check if this is lunch block (just show 1st/2nd/3rd lunch then)
		if (block.letter.equals("L"))
			return new Pair<>(LUNCHES.get(block.num - 1), roomNumText);
		if (isCancelledClass(block, yourAbsentTeachersBlocks))
			return new Pair<>(CANCELLED_CLASS, "");
		if (teacher == null)
			return new Pair<>(FREE_BLOCK, "");
		return new Pair<>(teacher.getNameOrSubject(), roomNumText);
	}
	public Pair<String, String> teacherTextAndRoomNumForBlock(Block block)
	{
		return teacherTextAndRoomNumForBlock(block, Settings.SINGLETON.getYourAbsentTeachersBlocks());
	}
	public boolean isFreeBlock(Block block)
	{
		return Settings.SINGLETON.getTeacherForBlock(block) == null;
	}
	//yourAbsentTeachersBlocks provided in parameters to save on CPU (only retrieve Set once in a loop or a list)
	public boolean isCancelledClass(Block block, Set<String> yourAbsentTeachersBlocks)
	{
		return yourAbsentTeachersBlocks.contains(block.getBlock());
	}
	//assumes you want to know if the block is cancelled FOR TODAY
	public boolean isCancelledClass(Block block)
	{
		Set<String> yourAbsentTeachersBlocks =  Settings.SINGLETON.getYourAbsentTeachersBlocks();
		return yourAbsentTeachersBlocks.contains(block.getBlock());
	}
}
