package com.nshsappdesignteam.nshsguide.util;


import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;


public final class SchoolYear
{
	//suppress default constructor
	private SchoolYear(){}
	public static LocalDate start()
	{
		LocalDate today = LocalDate.now();

		if (today.getMonthOfYear() < DateTimeConstants.SEPTEMBER)
			return new LocalDate(today.getYear() - 1, DateTimeConstants.SEPTEMBER, 1);
		else
			return new LocalDate(today.getYear(), DateTimeConstants.SEPTEMBER, 1);
	}
	public static LocalDate end()
	{
		LocalDate today = LocalDate.now();

		if (today.getMonthOfYear() < DateTimeConstants.SEPTEMBER)
			return new LocalDate(today.getYear(), DateTimeConstants.AUGUST, 31);
		else
			return new LocalDate(today.getYear() + 1, DateTimeConstants.AUGUST, 31);
	}
}
