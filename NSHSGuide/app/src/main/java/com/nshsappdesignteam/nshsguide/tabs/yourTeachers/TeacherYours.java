package com.nshsappdesignteam.nshsguide.tabs.yourTeachers;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableSet;
import com.nshsappdesignteam.nshsguide.helper.Teacher;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.util.Settings;

import java.util.HashSet;
import java.util.Set;

public class TeacherYours implements Teacher, Parcelable, HasType<YourTeacherListType>
{
	private final String name;
	public final String blockLetter;
	public final ImmutableSet<Integer> blockNums;
	public final int lunch;
	public final String subject;
	public final String roomNum;

	private TeacherYours(String name, String blockLetter, ImmutableSet<Integer> blockNums, int lunch, @NonNull String subject, @NonNull String roomNum)
	{
		this.name = name;
		this.blockLetter = blockLetter;
		this.blockNums = blockNums;
		this.lunch = lunch;
		this.subject = subject;
		this.roomNum = roomNum;
	}
	public void save()
	{
		TeacherSingleBlock.Builder builder = new TeacherSingleBlock.Builder();
		builder.name = name;
		builder.blockLetter = blockLetter;
		builder.subject = subject;
		builder.roomNum = roomNum;

		for (int blockNum : blockNums)
		{
			builder.blockNum = blockNum;
			Settings.SINGLETON.setTeacherForBlock(builder.build());
		}
		if (lunch != 0)
			Settings.SINGLETON.setLunchNumForBlockLetter(lunch, blockLetter);
	}
	public void remove()
	{
		for (int blockNum : blockNums)
			Settings.SINGLETON.removeTeacherForBlock(blockLetter + blockNum);
		if (lunch != 0)
			Settings.SINGLETON.removeLunchNumForBlockLetter(blockLetter);
	}
	@Override
	public String getName()
	{
		return name;
	}
	@Override
	public YourTeacherListType getType()
	{
		return subject.isEmpty() ? YourTeacherListType.YourTeacherNoSubject : YourTeacherListType.YourTeacher;
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TeacherYours)
		{
			TeacherYours t = (TeacherYours) o;
			return t.name.equals(name) && t.blockLetter.equals(blockLetter) && t.lunch == lunch && t.blockNums.equals(blockNums) && t.subject.equals(subject) && t.roomNum.equals(roomNum);
		}
		return false;
	}
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + name.hashCode();
		hash = hash * 31 + blockLetter.hashCode();
		hash = hash * 31 + blockNums.hashCode();
		hash = hash * 31 + lunch;
		hash = hash * 31 + subject.hashCode();
		hash = hash * 31 + roomNum.hashCode();
		return hash;
	}
	/*
		BUILDER CLASS FOR ADDTEACHERACTIVITY (WITH UNSAFE, MUTABLE FIELDS)
		 */
	public static class Builder
	{
		public String name;
		public String blockLetter;
		public Set<Integer> blockNums = new HashSet<>(4);
		public int lunch;
		public String subject;
		public String roomNum;

		public TeacherYours build()
		{
			return new TeacherYours(name, blockLetter, ImmutableSet.copyOf(blockNums), lunch, subject, roomNum);
		}
	}

	/*
	PARCEL STUFF SO TEACHERS CAN BE PASSED FROM YOUR TEACHERS LIST TO ADD TEACHERS ACTIVITY
	*/
	private TeacherYours(Parcel in)
	{
		name = in.readString();
		blockLetter = in.readString();
		Set<Integer> blockNums = (Set) in.readSerializable();
		this.blockNums = ImmutableSet.copyOf(blockNums);
		lunch = in.readInt();
		subject = in.readString();
		roomNum = in.readString();
	}
	@Override
	public int describeContents()
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(name);
		out.writeString(blockLetter);
		out.writeSerializable(blockNums);
		out.writeInt(lunch);
		out.writeString(subject);
		out.writeString(roomNum);
	}
	public static final Parcelable.Creator<TeacherYours> CREATOR = new Parcelable.Creator<TeacherYours>()
	{
		public TeacherYours createFromParcel(Parcel in)
		{
			return new TeacherYours(in);
		}

		public TeacherYours[] newArray(int size)
		{
			return new TeacherYours[size];
		}
	};
}
