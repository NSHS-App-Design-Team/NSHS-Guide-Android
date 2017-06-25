package com.nshsappdesignteam.nshsguide.helper.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VHClickable extends RecyclerView.ViewHolder implements View.OnClickListener
{
	protected final OnVHClickListener parent;

	public VHClickable(View itemView, OnVHClickListener parent)
	{
		super(itemView);
		this.parent = parent;
		itemView.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		parent.onItemClick(getAdapterPosition(), this);
	}
}
