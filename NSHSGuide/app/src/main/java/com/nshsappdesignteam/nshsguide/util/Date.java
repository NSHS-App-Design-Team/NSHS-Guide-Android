package com.nshsappdesignteam.nshsguide.util;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public final class Date
{
	private Date(){}
	public static boolean sameAsToday(LocalDate date)
	{
		return isSameAsDate(date, LocalDate.now());
	}
	public static boolean isSameAsDate(LocalDate date1, LocalDate date2)
	{
		return date1.getDayOfMonth() == date2.getDayOfMonth() && date1.getMonthOfYear() == date2.getMonthOfYear() && date1.getYear() == date2.getYear();
	}
	public static boolean isBeforeDate(LocalDate date, LocalDate laterDate)
	{
		if (isSameAsDate(date, laterDate))
			return false;
		return date.compareTo(laterDate) < 0;
	}
	public static boolean isWeekend(LocalDate date)
	{
		int dayOfWeek = date.getDayOfWeek();
		return dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY;
	}
}
