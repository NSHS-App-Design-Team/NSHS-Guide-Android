package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasInfo;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHClickListener;

public class ListYourAbsentTeacher extends ListYourAbsentTeacherNoInfo implements View.OnClickListener
{
	private final TextView infoText;

	public ListYourAbsentTeacher(View itemView, OnVHClickListener parent)
	{
		super(itemView, parent);
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
