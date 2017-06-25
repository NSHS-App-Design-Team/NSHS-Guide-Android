package com.nshsappdesignteam.nshsguide;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nshsappdesignteam.nshsguide.util.Internet;

import org.json.JSONObject;

import java.util.Set;

public class GcmListener extends GcmListenerService
{
	@Override
	public void onMessageReceived(String from, Bundle data)
	{
		Log.i("GcmListener", "onMessageReceived called");
		JSONObject extrasAsJSON = bundleToJSONObject(data);
		Internet.SINGLETON.performActionWithJSONAndNotify(extrasAsJSON, true);
	}
	private JSONObject bundleToJSONObject(Bundle bundle)
	{
		JSONObject jsonObject = new JSONObject();
		Set<String> keys = bundle.keySet();
		for (String key : keys)
		{
			try
			{
				jsonObject.put(key, bundle.get(key));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Log.wtf("GcmListener", "bundleToJSON failed");
			}
		}
		return jsonObject;
	}
}
