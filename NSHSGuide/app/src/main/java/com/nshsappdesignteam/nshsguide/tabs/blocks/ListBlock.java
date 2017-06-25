package com.nshsappdesignteam.nshsguide.tabs.blocks;

import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;

public class ListBlock extends ListBlockNoRoomNum
{
	private final TextView roomNumText;
	public ListBlock(View itemView)
	{
		super(itemView);
		roomNumText = (TextView) itemView.findViewById(R.id.roomNumText);
	}
	@Override
	public void updateContent(HasType<BlocksListType> content)
	{
		super.updateContent(content);
		roomNumText.setText(((TeacherWithBlock) content).teacher.roomNum);
	}
}
