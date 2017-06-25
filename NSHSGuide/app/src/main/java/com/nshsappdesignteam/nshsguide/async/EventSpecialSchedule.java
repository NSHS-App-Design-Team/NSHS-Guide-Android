package com.nshsappdesignteam.nshsguide.async;

public class EventSpecialSchedule implements EventBoolean
{
	private boolean exists;
	@Override
	public void setSuccessful(boolean successful)
	{
		exists = successful;
	}
	@Override
	public boolean successful()
	{
		return exists;
	}
}
