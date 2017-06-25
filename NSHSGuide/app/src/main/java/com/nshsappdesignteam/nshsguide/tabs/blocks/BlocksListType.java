package com.nshsappdesignteam.nshsguide.tabs.blocks;

public enum BlocksListType
{
	Subheader(0), SubheaderWithStar(1), Block(2), BlockNoRoomNum(3);

	public final int value;
	BlocksListType(int value)
	{
		this.value = value;
	}
}
