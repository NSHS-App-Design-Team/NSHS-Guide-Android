package com.nshsappdesignteam.nshsguide.helper.recyclerview;

import android.view.View;

public abstract class VHLongClickUpdatable<T extends Enum<T>> extends VHLongClickable implements UpdatableOfType<T>
{
	public VHLongClickUpdatable(View itemView, OnVHLongClickListener parent)
	{
		super(itemView, parent);
	}
}
