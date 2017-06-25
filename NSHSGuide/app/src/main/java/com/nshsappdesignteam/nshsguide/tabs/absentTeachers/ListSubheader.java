package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.InfoOfType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHClickListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHClickUpdatable;

public class ListSubheader extends VHClickUpdatable<AbsentTeacherListType>
{
	private final TextView subheader;

	public ListSubheader(View itemView, OnVHClickListener parent)
	{
		super(itemView, parent);
		subheader = (TextView) itemView.findViewById(R.id.subheaderText);
	}
	@Override
	public void updateContent(HasType<AbsentTeacherListType> content)
	{
		InfoOfType infoOfType = (InfoOfType) content;
		subheader.setText(infoOfType.getInfo());
	}
}
