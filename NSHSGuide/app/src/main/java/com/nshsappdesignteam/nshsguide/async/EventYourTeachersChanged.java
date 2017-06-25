package com.nshsappdesignteam.nshsguide.async;

public class EventYourTeachersChanged implements EventBoolean
{
	private boolean teachersChanged;

	public EventYourTeachersChanged(boolean teachersChanged)
	{
		this.teachersChanged = teachersChanged;
	}
	@Override
	public void setSuccessful(boolean successful)
	{
		teachersChanged = successful;
	}
	@Override
	public boolean successful()
	{
		return teachersChanged;
	}
}
