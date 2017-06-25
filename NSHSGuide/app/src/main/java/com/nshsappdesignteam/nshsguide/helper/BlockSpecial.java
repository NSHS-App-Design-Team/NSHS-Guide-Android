package com.nshsappdesignteam.nshsguide.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BlockSpecial extends Block
{
	public final String customName;
	public final Time firstLunchEnd, secondLunchStart, secondLunchEnd, thirdLunchStart;

	public BlockSpecial(String letter, int number, Time startTime, Time endTime, boolean isLunchBlock, @NonNull String customName,
	                    @Nullable Time firstLunchEnd, @Nullable Time secondLunchStart, @Nullable Time secondLunchEnd, @Nullable Time thirdLunchStart)
	{
		super(letter, number, startTime, endTime, isLunchBlock);
		this.customName = customName;
		this.firstLunchEnd = firstLunchEnd;
		this.secondLunchStart = secondLunchStart;
		this.secondLunchEnd = secondLunchEnd;
		this.thirdLunchStart = thirdLunchStart;
	}

	public static class Builder
	{
		public String letter;
		public int num;
		public Time startTime;
		public Time endTime;
		public boolean isLunch;
		@NonNull
		public String customName = "";
		public Time firstLunchEnd = null;
		public Time secondLunchStart = null;
		public Time secondLunchEnd = null;
		public Time thirdLunchStart = null;
		public BlockSpecial build()
		{
			return new BlockSpecial(letter, num, startTime, endTime, isLunch, customName, firstLunchEnd, secondLunchStart, secondLunchEnd, thirdLunchStart);
		}
	}
	//string in following format: "A|2|7:45|8:55|true|Custom block|8:00|8:05|8:30|8:35"
	@Override
	public String toString()
	{
		String output = super.toString() + "|" + customName;    //if customName is empty, stored anyway as || instead of |customName|
		if (firstLunchEnd != null)
			output += "|" + firstLunchEnd + "|" + secondLunchStart + "|" + secondLunchEnd + "|" + thirdLunchStart;
		return output;
	}
	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = hash * 31 + customName.hashCode();
		if (firstLunchEnd != null)
		{
			hash = hash * 31 + firstLunchEnd.hashCode();
			hash = hash * 31 + secondLunchStart.hashCode();
			hash = hash * 31 + secondLunchEnd.hashCode();
			hash = hash * 31 + thirdLunchStart.hashCode();
		}
		return hash;
	}
	@Override
	public boolean equals(Object o)
	{
		if (!super.equals(o))
			return false;
		if (o instanceof BlockSpecial)
		{
			BlockSpecial b = (BlockSpecial) o;
			if (firstLunchEnd == null)
				return b.customName.equals(customName);
			else
				return b.customName.equals(customName) && b.firstLunchEnd.equals(firstLunchEnd) && b.secondLunchStart.equals(secondLunchStart) && b.secondLunchEnd.equals(secondLunchEnd) && b.thirdLunchStart.equals(thirdLunchStart);
		}
		return false;
	}
}
