package com.nshsappdesignteam.nshsguide.helper.recyclerview;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import com.nshsappdesignteam.nshsguide.R;

public class VHLongClickable extends VHClickable implements View.OnLongClickListener
{
	private final Drawable defaultDrawable;
	private boolean selected = false;

	public VHLongClickable(View itemView, OnVHLongClickListener parent)
	{
		super(itemView, parent);

		int[] attrs = new int[]{android.R.attr.selectableItemBackground};
		TypedArray typedArray = itemView.getContext().obtainStyledAttributes(attrs);
		defaultDrawable = typedArray.getDrawable(0);
		typedArray.recycle();

		itemView.setOnLongClickListener(this);
	}
	@Override
	public boolean onLongClick(View v)
	{
		((OnVHLongClickListener) parent).onItemLongClick(getAdapterPosition(), this);
		return true;
	}
	public void setSelected(boolean selected)
	{
		this.selected = selected;

		if (selected)
			itemView.setBackgroundResource(R.color.blueGreyLight);
		else
		{
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
				itemView.setBackgroundDrawable(defaultDrawable);
			else
				itemView.setBackground(defaultDrawable);
		}
	}
	public boolean isSelected()
	{
		return selected;
	}
}
