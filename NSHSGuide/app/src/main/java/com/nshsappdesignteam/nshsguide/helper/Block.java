package com.nshsappdesignteam.nshsguide.helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.joda.time.DateTimeConstants;

import java.util.List;
import java.util.Map;

public class Block implements Parcelable
{
	public static final ImmutableList<String> BLOCK_LETTERS = ImmutableList.<String>builder()
			.add("A", "B", "C", "D", "E", "F", "G", "HR", "J")
			.build();
	public static final int[] BLOCK_NUMS = new int[]{1, 2, 3, 4};
	private static final List<Block> BLOCKS_ON_MONDAY = ImmutableList.<Block>builder()
			.add(new Block("A", 1, new Time(7, 40), new Time(8, 35), false))
			.add(new Block("B", 1, new Time(8, 40), new Time(9, 35), false))
			.add(new Block("HR", 1, new Time(9, 40), new Time(9, 45), false))
			.add(new Block("C", 1, new Time(9, 50), new Time(10, 45), false))
			.add(new Block("D", 1, new Time(10, 50), new Time(12, 35), true))
			.add(new Block("E", 1, new Time(12, 40), new Time(13, 35), false))
			.add(new Block("F", 1, new Time(13, 40), new Time(14, 35), false))
			.add(new Block("J", 1, new Time(14, 40), new Time(15, 20), false))
			.build();
	private static final List<Block> BLOCKS_ON_TUESDAY = ImmutableList.<Block>builder()
			.add(new Block("G", 1, new Time(7, 40), new Time(8, 35), false))
			.add(new Block("F", 2, new Time(8, 40), new Time(9, 35), false))
			.add(new Block("HR", 2, new Time(9, 40), new Time(10, 5), false))
			.add(new Block("C", 2, new Time(10, 10), new Time(11, 5), false))
			.add(new Block("E", 2, new Time(11, 10), new Time(12, 55), true))
			.add(new Block("D", 2, new Time(13, 0), new Time(13, 55), false))
			.build();
	private static final List<Block> BLOCKS_ON_WEDNESDAY = ImmutableList.<Block>builder()
			.add(new Block("A", 2, new Time(7, 40), new Time(8, 55), false))
			.add(new Block("B", 2, new Time(9, 0), new Time(9, 55), false))
			.add(new Block("G", 2, new Time(10, 0), new Time(10, 55), false))
			.add(new Block("F", 3, new Time(11, 0), new Time(12, 45), true))
			.add(new Block("D", 3, new Time(12, 50), new Time(13, 45), false))
			.add(new Block("E", 3, new Time(13, 50), new Time(14, 45), false))
			.add(new Block("J", 2, new Time(14, 50), new Time(15, 20), false))
			.build();
	private static final List<Block> BLOCKS_ON_THURSDAY = ImmutableList.<Block>builder()
			.add(new Block("A", 3, new Time(7, 40), new Time(8, 35), false))
			.add(new Block("B", 3, new Time(8, 40), new Time(9, 35), false))
			.add(new Block("HR", 3, new Time(9, 40), new Time(9, 45), false))
			.add(new Block("F", 4, new Time(9, 50), new Time(10, 45), false))
			.add(new Block("G", 3, new Time(10, 50), new Time(12, 35), true))
			.add(new Block("E", 4, new Time(12, 40), new Time(13, 35), false))
			.add(new Block("C", 3, new Time(13, 40), new Time(14, 35), false))
			.add(new Block("J", 3, new Time(14, 40), new Time(15, 20), false))
			.build();
	private static final List<Block> BLOCKS_ON_FRIDAY = ImmutableList.<Block>builder()
			.add(new Block("A", 4, new Time(7, 40), new Time(8, 35), false))
			.add(new Block("B", 4, new Time(8, 40), new Time(9, 55), false))
			.add(new Block("HR", 4, new Time(10, 0), new Time(10, 5), false))
			.add(new Block("G", 4, new Time(10, 10), new Time(11, 5), false))
			.add(new Block("C", 4, new Time(11, 10), new Time(12, 55), true))
			.add(new Block("D", 4, new Time(13, 0), new Time(13, 55), false))
			.build();
	public static final Map<Integer, List<Block>> BLOCKS_GIVEN_DAY = ImmutableMap.<Integer, List<Block>>builder()
			.put(DateTimeConstants.MONDAY, BLOCKS_ON_MONDAY)
			.put(DateTimeConstants.TUESDAY, BLOCKS_ON_TUESDAY)
			.put(DateTimeConstants.WEDNESDAY, BLOCKS_ON_WEDNESDAY)
			.put(DateTimeConstants.THURSDAY, BLOCKS_ON_THURSDAY)
			.put(DateTimeConstants.FRIDAY, BLOCKS_ON_FRIDAY)
			.build();
	private static final int TIME_SECOND_LUNCH_CLASS_BEFORE_LUNCH = 35;
	private static final int TIME_LUNCH = 30;
	//don't use SparseArray<String> because it has no .containsValue() method, and indexAtValue() doesn't work for Strings
	public static final ImmutableMap<Integer, String> LUNCH_BLOCK_GIVEN_DAY = ImmutableMap.<Integer, String>builder()
			.put(DateTimeConstants.MONDAY, "D1")
			.put(DateTimeConstants.TUESDAY, "E2")
			.put(DateTimeConstants.WEDNESDAY, "F3")
			.put(DateTimeConstants.THURSDAY, "G3")
			.put(DateTimeConstants.FRIDAY, "C4")
			.build();
	public final String letter;
	public final int num;
	public final Time startTime, endTime;
	public final boolean isLunchBlock;

	public Block(String letter, int number, Time startTime, Time endTime, boolean isLunchBlock)
	{
		this.letter = letter;
		num = number;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isLunchBlock = isLunchBlock;
	}
	public String getBlock()
	{
		//lunches don't have numbers
		return shouldHideNum() ? letter : letter + num;
	}
	public SpannableStringBuilder getBlockWithNumSubscript()
	{
		if (shouldHideNum())
			return new SpannableStringBuilder(letter);

		SpannableStringBuilder text = new SpannableStringBuilder(getBlock());
		//let the number be half as big as the letter
		text.setSpan(new RelativeSizeSpan(0.5f), text.length() - 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return text;
	}
	private boolean shouldHideNum()
	{
		return letter.equals("L") || num == 0;
	}
	public String getTimeString()
	{
		return startTime + " ~ " + endTime;
	}
	//bad naming, but this method returns the lunch time of a particular lunch block as a block object
	public static Block createLunchAsBlock(int lunchNum, Block classBlock)
	{
		Time lunchStartTime;
		Time lunchEndTime;
		if (classBlock instanceof BlockSpecial && ((BlockSpecial) classBlock).firstLunchEnd != null)
		{
			switch (lunchNum)
			{
				case 1:
					lunchStartTime = classBlock.startTime;
					lunchEndTime = ((BlockSpecial) classBlock).firstLunchEnd;
					break;
				case 2:
					lunchStartTime = ((BlockSpecial) classBlock).secondLunchStart;
					lunchEndTime = ((BlockSpecial) classBlock).secondLunchEnd;
					break;
				case 3:
					lunchStartTime = ((BlockSpecial) classBlock).thirdLunchStart;
					lunchEndTime = classBlock.endTime;
					break;
				default:
					lunchStartTime = new Time(0, 0);
					lunchEndTime = new Time(0, 0);
					Log.wtf("Block", "createLunchAsBlock weird lunch: " + lunchNum);
			}
		}
		else
		{
			switch (lunchNum)
			{
				case 1:
					lunchStartTime = classBlock.startTime;
					lunchEndTime = Time.add(lunchStartTime, TIME_LUNCH);
					break;
				case 2:
					lunchStartTime = Time.add(classBlock.startTime, TIME_SECOND_LUNCH_CLASS_BEFORE_LUNCH);
					lunchEndTime = Time.add(lunchStartTime, TIME_LUNCH);
					break;
				case 3:
					lunchStartTime = Time.add(classBlock.endTime, -TIME_LUNCH);
					lunchEndTime = classBlock.endTime;
					break;
				default:
					lunchStartTime = new Time(0, 0);
					lunchEndTime = new Time(0, 0);
					Log.wtf("Block", "createLunchAsBlock weird lunch: " + lunchNum);
			}
		}
		return new Block("L", lunchNum, lunchStartTime, lunchEndTime, false);
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Block)
		{
			Block b = (Block) o;
			return b.letter.equals(letter) && b.num == num && b.startTime.equals(startTime) && b.endTime.equals(endTime) && b.isLunchBlock == isLunchBlock;
		}
		return false;
	}
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + letter.hashCode();
		hash = hash * 31 + num;
		hash = hash * 31 + startTime.hashCode();
		hash = hash * 31 + endTime.hashCode();
		hash = hash * 31 + (isLunchBlock ? 0 : 1);
		return hash;
	}
	@Override
	public String toString()
	{
		return letter + "|" + num + "|" + startTime + "|" + endTime + "|" + isLunchBlock;
	}
	public static Block fromString(String string)
	{
		String[] parts = string.split("\\|");
		String letter = parts[0];
		int num = Integer.parseInt(parts[1]);
		Time startTime = Time.fromString(parts[2]);
		Time endTime = Time.fromString(parts[3]);
		boolean isLunchBlock = Boolean.parseBoolean(parts[4]);
		if (parts.length == 5)
			return new Block(letter, num, startTime, endTime, isLunchBlock);

		//must be a special block
		String customName = parts[5];
		Time firstLunchEnd = null;
		Time secondLunchStart = null;
		Time secondLunchEnd = null;
		Time thirdLunchStart = null;

		if (parts.length == 10)
		{
			firstLunchEnd = Time.fromString(parts[6]);
			secondLunchStart = Time.fromString(parts[7]);
			secondLunchEnd = Time.fromString(parts[8]);
			thirdLunchStart = Time.fromString(parts[9]);
		}
		return new BlockSpecial(letter, num, startTime, endTime, isLunchBlock, customName, firstLunchEnd,
				secondLunchStart, secondLunchEnd, thirdLunchStart);
	}
	/*
			PARCEL STUFF SO BLOCK NOTIFICATIONS IS MORE CONVENIENT
			 */
	private Block(Parcel in)
	{
		//must be written to in the correct order to match properties
		letter = in.readString();
		num = in.readInt();
		startTime = in.readParcelable(Time.class.getClassLoader());
		endTime = in.readParcelable(Time.class.getClassLoader());
		isLunchBlock = in.readByte() != 0;
	}
	@Override
	public int describeContents()
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(letter);
		out.writeInt(num);
		out.writeParcelable(startTime, 0);
		out.writeParcelable(endTime, 0);
		out.writeByte((byte) (isLunchBlock ? 1 : 0));
	}
	public static final Parcelable.Creator<Block> CREATOR = new Parcelable.Creator<Block>()
	{
		public Block createFromParcel(Parcel in)
		{
			return new Block(in);
		}

		public Block[] newArray(int size)
		{
			return new Block[size];
		}
	};
}
