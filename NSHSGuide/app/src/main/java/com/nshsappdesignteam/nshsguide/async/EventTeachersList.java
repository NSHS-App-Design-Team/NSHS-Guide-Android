package com.nshsappdesignteam.nshsguide.async;

public class EventTeachersList implements EventBoolean
{
	private boolean gotTeachersList;
	@Override
	public void setSuccessful(boolean successful)
	{
		gotTeachersList = successful;
	}
	@Override
	public boolean successful()
	{
		return gotTeachersList;
	}
}
