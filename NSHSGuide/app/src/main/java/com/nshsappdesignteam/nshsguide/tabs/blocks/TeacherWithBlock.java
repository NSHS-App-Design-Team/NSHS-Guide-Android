package com.nshsappdesignteam.nshsguide.tabs.blocks;

import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.tabs.yourTeachers.TeacherSingleBlock;

public class TeacherWithBlock implements HasType<BlocksListType>
{
	public final TeacherSingleBlock teacher;
	public final Block block;
	public TeacherWithBlock(TeacherSingleBlock teacher, Block block)
	{
		this.teacher = teacher;
		this.block = block;
	}
	@Override
	public int hashCode()
	{
		return teacher.hashCode() * 31 + block.hashCode();
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TeacherWithBlock)
		{
			TeacherWithBlock t = (TeacherWithBlock) o;
			return t.teacher.equals(teacher) && t.block.equals(block);
		}
		return false;
	}
	@Override
	public BlocksListType getType()
	{
		return teacher.roomNum.isEmpty() ? BlocksListType.BlockNoRoomNum : BlocksListType.Block;
	}
}
