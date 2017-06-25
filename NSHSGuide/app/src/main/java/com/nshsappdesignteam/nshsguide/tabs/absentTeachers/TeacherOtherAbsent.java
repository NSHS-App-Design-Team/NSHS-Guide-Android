package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;

import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.Teacher;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasInfo;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;

public class TeacherOtherAbsent implements Teacher, Comparable<TeacherOtherAbsent>, HasType<AbsentTeacherListType>, HasInfo
{
	//interpret the 0s & 1s in the last part of the teachers' info
	private static final char ABSENT = '1';
	private final String name;
	private final String info;
	public final SimpleArrayMap<String, Boolean> absentForBlock;

	private TeacherOtherAbsent(String name, @NonNull String info, SimpleArrayMap<String, Boolean> absentForBlock)
	{
		this.name = name;
		this.info = info;
		this.absentForBlock = absentForBlock;
	}
	public static TeacherOtherAbsent fromString(String string)
	{
		//assumes absent teacher is stored as such: "Chu, David|010011100|info"
		String[] teacherWithInfo = string.split("\\|", -1);
		String name = teacherWithInfo[0];
		String info = teacherWithInfo[2];
		char[] absentBlocks = teacherWithInfo[1].toCharArray();

		return new TeacherOtherAbsent(name, info, absentBlocksMapFromArray(absentBlocks));
	}
	private static SimpleArrayMap<String, Boolean> absentBlocksMapFromArray(char[] absentBlocks)
	{
		SimpleArrayMap<String, Boolean> absentForBlock = new SimpleArrayMap<>(Block.BLOCK_LETTERS.size());
		for (int i = 0; i < Block.BLOCK_LETTERS.size(); i++)
		{
			char teacherAbsentForThisBlock = absentBlocks[i];    //either '0' or '1'
			String blockLetter = Block.BLOCK_LETTERS.get(i);    //010011100 corresponds to ABCDEFGHJ blocks
			absentForBlock.put(blockLetter, teacherAbsentForThisBlock == ABSENT);
		}
		return absentForBlock;
	}
	@Override
	public AbsentTeacherListType getType()
	{
		return info.isEmpty() ? AbsentTeacherListType.OtherTeacherNoInfo : AbsentTeacherListType.OtherTeacher;
	}
	@Override
	public String getName()
	{
		return name;
	}
	@NonNull
	@Override
	public String getInfo()
	{
		return info;
	}
	@Override
	public int compareTo(@NonNull TeacherOtherAbsent another)
	{
		return name.compareTo(another.name);
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TeacherOtherAbsent)
		{
			TeacherOtherAbsent t = (TeacherOtherAbsent) o;
			return t.name.equals(name) && t.info.equals(info) && t.absentForBlock.equals(absentForBlock);
		}
		return false;
	}
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + name.hashCode();
		hash = hash * 31 + info.hashCode();
		for (String blockLetter : Block.BLOCK_LETTERS)
			hash = hash * 31 + (absentForBlock.get(blockLetter) ? 0 : 1);
		return hash;
	}
}
