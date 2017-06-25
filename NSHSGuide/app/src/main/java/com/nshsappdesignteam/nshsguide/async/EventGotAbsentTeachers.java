package com.nshsappdesignteam.nshsguide.async;

//event passed by eventBus when asyncTask wants someone's swipe refresh to finish
public class EventGotAbsentTeachers implements EventBoolean
{
	private boolean shouldPopulateList;
	@Override
	public void setSuccessful(boolean successful)
	{
		shouldPopulateList = successful;
	}
	@Override
	public boolean successful()
	{
		return shouldPopulateList;
	}
}
