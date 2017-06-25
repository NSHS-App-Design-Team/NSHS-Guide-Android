package com.nshsappdesignteam.nshsguide.async;

public class EventGotAnnouncement implements EventBoolean
{
	private boolean gotAnnouncement;
	@Override
	public void setSuccessful(boolean successful)
	{
		gotAnnouncement = successful;
	}
	@Override
	public boolean successful()
	{
		return gotAnnouncement;
	}
}
