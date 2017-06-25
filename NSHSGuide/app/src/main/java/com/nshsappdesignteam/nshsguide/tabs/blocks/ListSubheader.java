package com.nshsappdesignteam.nshsguide.tabs.blocks;

import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.InfoOfType;

public class ListSubheader extends BlocksUpdatableVH
{
	private final TextView subheader;

	public ListSubheader(View itemView)
	{
		super(itemView);
		subheader = (TextView) itemView.findViewById(R.id.subheaderText);
	}
	@Override
	public void updateContent(HasType<BlocksListType> content)
	{
		InfoOfType infoOfType = (InfoOfType) content;
		subheader.setText(infoOfType.getInfo());
	}
}
