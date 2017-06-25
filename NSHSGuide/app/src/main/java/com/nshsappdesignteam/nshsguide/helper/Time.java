package com.nshsappdesignteam.nshsguide.helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;

public class Time implements Parcelable
{
	public final int hour, minute;

	public Time(@IntRange(from = 0) int hour, @IntRange(from = 0) int minute)
	{
		this.hour = hour;
		this.minute = minute;
	}
	@Override
	public String toString()
	{
		int hour12 = hour > 12 ? hour - 12 : hour;
		return String.format("%01d:%02d", hour12, minute);
	}
	public static Time fromString(String timeString)
	{
		String[] timeStringArray = timeString.split(":");
		return new Time(Integer.valueOf(timeStringArray[0]), Integer.valueOf(timeStringArray[1]));
	}
	public int toMinutes()
	{
		return hour * 60 + minute;
	}
	public static Time fromMinutes(@IntRange(from = 0) int minutes) //minutes must be positive
	{
		int hours = minutes / 60;
		int remainingMinutes = minutes % 60;
		return new Time(hours, remainingMinutes);
	}
	public static Time add(Time startTime, int minutesToAdd)
	{
		int timeInMinutes = startTime.toMinutes();
		timeInMinutes += minutesToAdd;
		return Time.fromMinutes(timeInMinutes);
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Time)
		{
			Time t = (Time) o;
			return t.hour == hour && t.minute == minute;
		}
		return false;
	}
	@Override
	public int hashCode()
	{
		return toMinutes();
	}

	/*
	PARCEL STUFF SO BLOCK CAN WRITE THIS
	*/
	private Time(Parcel in)
	{
		hour = in.readInt();
		minute = in.readInt();
	}
	@Override
	public int describeContents()
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeInt(hour);
		out.writeInt(minute);
	}
	public static final Parcelable.Creator<Time> CREATOR = new Parcelable.Creator<Time>()
	{
		public Time createFromParcel(Parcel in)
		{
			return new Time(in);
		}

		public Time[] newArray(int size)
		{
			return new Time[size];
		}
	};
}
