package com.nshsappdesignteam.nshsguide.async;

import com.google.common.eventbus.EventBus;

//this class basically exists just to hold a static "eventBus" reference for all to use
public class MyEventBus
{
	public static final EventBus eventBus = new EventBus();
}
