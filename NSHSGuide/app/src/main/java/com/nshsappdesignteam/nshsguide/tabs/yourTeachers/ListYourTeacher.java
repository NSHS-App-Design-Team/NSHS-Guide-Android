package com.nshsappdesignteam.nshsguide.tabs.yourTeachers;

import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHLongClickListener;

public class ListYourTeacher extends ListYourTeacherNoSubject
{
	private final TextView subjectText;

	public ListYourTeacher(View itemView, OnVHLongClickListener parent)
	{
		super(itemView, parent);
		subjectText = (TextView) itemView.findViewById(R.id.subjectText);
	}
	@Override
	public void updateContent(HasType<YourTeacherListType> content)
	{
		super.updateContent(content);
		TeacherYours teacher = (TeacherYours) content;
		subjectText.setText(teacher.subject);
	}
}
