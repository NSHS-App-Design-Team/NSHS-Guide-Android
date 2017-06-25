package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasInfo;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHClickListener;

import java.util.List;

public class ListOtherTeacher extends ListOtherTeacherNoInfo
{
	private final TextView infoText;

	public ListOtherTeacher(View itemView, List<Block> blocks, OnVHClickListener parent, Context context)
	{
		super(itemView, blocks, parent, context);
		infoText = (TextView) itemView.findViewById(R.id.infoText);
	}
	@Override
	public void updateContent(HasType<AbsentTeacherListType> content)
	{
		super.updateContent(content);
		HasInfo infoContainer = (HasInfo) content;
		infoText.setText(infoContainer.getInfo());
	}
}
