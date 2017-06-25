package com.nshsappdesignteam.nshsguide.tabs.yourTeachers;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.nshsappdesignteam.nshsguide.helper.Teacher;

public class TeacherSingleBlock implements Teacher, Parcelable
{
	private final String name;
	public final String blockLetter;
	public final int blockNum;
	public final String subject;
	public final String roomNum;

	private TeacherSingleBlock(String name, String blockLetter, int blockNum, @NonNull String subject, @NonNull String roomNum)
	{
		this.name = name;
		this.blockLetter = blockLetter;
		this.blockNum = blockNum;
		this.subject = subject;
		this.roomNum = roomNum;
	}
	@Override
	public String getName()
	{
		return name;
	}
	public String getNameOrSubject()
	{
		return subject.isEmpty() ? name : subject;
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TeacherSingleBlock)
		{
			TeacherSingleBlock t = (TeacherSingleBlock) o;
			return t.name.equals(name) && t.blockLetter.equals(blockLetter) && t.blockNum == blockNum && t.subject.equals(subject) && t.roomNum.equals(roomNum);
		}
		return false;
	}
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 31 + name.hashCode();
		hash = hash * 31 + blockLetter.hashCode();
		hash = hash * 31 + blockNum;
		hash = hash * 31 + subject.hashCode();
		hash = hash * 31 + roomNum.hashCode();
		return hash;
	}

	public static class Builder
	{
		public String name;
		public String blockLetter;
		public int blockNum;
		@NonNull
		public String subject = "";
		@NonNull
		public String roomNum = "";

		public TeacherSingleBlock build()
		{
			return new TeacherSingleBlock(name, blockLetter, blockNum, subject, roomNum);
		}
	}

	/*
	PARCEL STUFF SO TEACHERS CAN BE PASSED FROM YOUR TEACHERS LIST TO ADD TEACHERS ACTIVITY
	*/
	private TeacherSingleBlock(Parcel in)
	{
		name = in.readString();
		blockLetter = in.readString();
		blockNum = in.readInt();
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
		out.writeInt(blockNum);
		out.writeString(subject);
		out.writeString(roomNum);
	}
	public static final Parcelable.Creator<TeacherSingleBlock> CREATOR = new Parcelable.Creator<TeacherSingleBlock>()
	{
		public TeacherSingleBlock createFromParcel(Parcel in)
		{
			return new TeacherSingleBlock(in);
		}

		public TeacherSingleBlock[] newArray(int size)
		{
			return new TeacherSingleBlock[size];
		}
	};
}
