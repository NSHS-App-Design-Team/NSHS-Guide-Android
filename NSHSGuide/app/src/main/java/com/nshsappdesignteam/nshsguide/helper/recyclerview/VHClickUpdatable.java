package com.nshsappdesignteam.nshsguide.helper.recyclerview;

import android.view.View;

public abstract class VHClickUpdatable<T extends Enum<T>> extends VHClickable implements UpdatableOfType<T>
{
	public VHClickUpdatable(View itemView, OnVHClickListener parent)
	{
		super(itemView, parent);
	}
}
