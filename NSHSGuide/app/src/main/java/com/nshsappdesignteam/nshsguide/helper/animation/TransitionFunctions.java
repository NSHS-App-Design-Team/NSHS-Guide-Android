package com.nshsappdesignteam.nshsguide.helper.animation;

public final class TransitionFunctions
{
	private TransitionFunctions(){}
	public static float interpolate(float start, float end, float fraction)
	{
		return start + (fraction * (end - start));
	}
}
