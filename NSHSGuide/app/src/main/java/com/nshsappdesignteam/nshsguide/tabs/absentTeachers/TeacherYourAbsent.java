package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.support.annotation.NonNull;

import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.Teacher;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasInfo;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;

public class TeacherYourAbsent implements Teacher, Comparable<TeacherYourAbsent>, HasType<AbsentTeacherListType>, HasInfo
{
	private final String name;
	private final String info;
	public final Block block;

	public TeacherYourAbsent(String name, @NonNull String info, Block block)
	{
		this.name = name;
		this.info = info;
		this.block = block;
	}
	@Override
	public AbsentTeacherListType getType()
	{
		return info.isEmpty() ? AbsentTeacherListType.YourTeacherNoInfo : AbsentTeacherListType.YourTeacher;
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
	public int compareTo(@NonNull TeacherYourAbsent another)
	{
		return Block.BLOCK_LETTERS.indexOf(block.letter) - Block.BLOCK_LETTERS.indexOf(another.block.letter);
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TeacherYourAbsent)
		{
			TeacherYourAbsent t = (TeacherYourAbsent) o;
			return t.name.equals(name) && t.info.equals(info) && t.block.equals(block);
		}
		return false;
	}
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + name.hashCode();
		hash = hash * 31 + info.hashCode();
		hash = hash * 31 + block.hashCode();
		return hash;
	}
}
