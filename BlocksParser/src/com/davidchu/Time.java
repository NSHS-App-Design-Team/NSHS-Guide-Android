package com.davidchu;

public class Time
{
	public final int hour, minute;

	public Time(int hour, int minute)
	{
		this.hour = hour;
		this.minute = minute;
	}
	public int toMinutes()
	{
		return hour * 60 + minute;
	}
	public static Time fromMinutes(int minutes) //minutes must be positive
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
	public String toString()
	{
		return String.format("%02d:%02d", hour, minute);
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
}

