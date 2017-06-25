package com.nshsappdesignteam.nshsguide.tabs.blocks;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nshsappdesignteam.nshsguide.helper.recyclerview.UpdatableOfType;

public abstract class BlocksUpdatableVH extends RecyclerView.ViewHolder implements UpdatableOfType<BlocksListType>
{
	public BlocksUpdatableVH(View itemView)
	{
		super(itemView);
	}
}
