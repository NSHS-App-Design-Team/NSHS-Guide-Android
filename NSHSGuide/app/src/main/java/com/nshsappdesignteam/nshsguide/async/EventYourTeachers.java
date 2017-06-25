package com.nshsappdesignteam.nshsguide.async;

import com.nshsappdesignteam.nshsguide.tabs.yourTeachers.TeacherYours;

import java.util.List;

public class EventYourTeachers
{
	public final List<TeacherYours> teachers;

	public EventYourTeachers(List<TeacherYours> teachers)
	{
		this.teachers = teachers;
	}
}
