package com.nshsappdesignteam.nshsguide.tabs.blocks;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;

import java.util.ArrayList;
import java.util.List;

public class BlockDayRecyclerAdapter extends RecyclerView.Adapter<BlocksUpdatableVH>
{
	private List<HasType<BlocksListType>> listItems = new ArrayList<>();

	@Override
	public BlocksUpdatableVH onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		if (viewType == BlocksListType.Subheader.value)
			return new ListSubheader(inflater.inflate(R.layout.list_subheader, parent, false));
		else if (viewType == BlocksListType.SubheaderWithStar.value)
			return new ListSubheader(inflater.inflate(R.layout.list_subheader_with_star, parent, false));
		else if (viewType == BlocksListType.BlockNoRoomNum.value)
			return new ListBlockNoRoomNum(inflater.inflate(R.layout.list_block_with_time_no_info, parent, false));
		else if (viewType == BlocksListType.Block.value)
			return new ListBlock(inflater.inflate(R.layout.list_block_with_time_room_num, parent, false));
		Log.wtf("BlockDayAdapter", "viewType not listed: " + viewType);
		return null;
	}
	@Override
	public void onBindViewHolder(BlocksUpdatableVH holder, int position)
	{
		holder.updateContent(listItems.get(position));
	}
	@Override
	public int getItemViewType(int position)
	{
		return listItems.get(position).getType().value;
	}
	@Override
	public int getItemCount()
	{
		return listItems.size();
	}
	public void addItems(List<HasType<BlocksListType>> newItems)
	{
		int startIndex = listItems.size();
		listItems.addAll(newItems);
		notifyItemRangeInserted(startIndex, newItems.size() - 1);
	}
	public void removeAll()
	{
		int count = listItems.size();
		listItems.clear();
		notifyItemRangeRemoved(0, count);
	}
}
