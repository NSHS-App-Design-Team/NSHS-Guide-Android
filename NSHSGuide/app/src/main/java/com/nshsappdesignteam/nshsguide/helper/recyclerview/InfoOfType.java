package com.nshsappdesignteam.nshsguide.helper.recyclerview;

import android.support.annotation.NonNull;

public class InfoOfType<T extends Enum<T>> implements HasType<T>, HasInfo
{
	private final String text;
	private final T type;

	public InfoOfType(String text, T type)
	{
		this.text = text;
		this.type = type;
	}
	@Override
	public T getType()
	{
		return type;
	}
	@NonNull
	@Override
	public String getInfo()
	{
		return text;
	}
}
