package com.nshsappdesignteam.nshsguide;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.nshsappdesignteam.nshsguide.util.Internet;

public class MyInstanceIdListener extends InstanceIDListenerService
{
	@Override
	public void onTokenRefresh()
	{
		Internet.SINGLETON.getRegIdIfNotRegistered(false);
	}
}
