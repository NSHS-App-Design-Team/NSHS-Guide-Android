package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

public enum AbsentTeacherListType
{
	Subheader(0), Announcement(1), YourTeacher(2), YourTeacherNoInfo(3), OtherTeacher(4), OtherTeacherNoInfo(5);

	public final int value;
	AbsentTeacherListType(int value)
	{
		this.value = value;
	}
}
