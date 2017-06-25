package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.BlockImage;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHClickListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHClickUpdatable;

public class ListYourAbsentTeacherNoInfo extends VHClickUpdatable<AbsentTeacherListType>
{
	private final TextView teacherNameText;
	private final BlockImage blockImage;
	private final TextView timeText;

	public ListYourAbsentTeacherNoInfo(View itemView, OnVHClickListener parent)
	{
		super(itemView, parent);
		teacherNameText = (TextView) itemView.findViewById(R.id.teacherText);
		blockImage = (BlockImage) itemView.findViewById(R.id.blockImage);
		timeText = (TextView) itemView.findViewById(R.id.timeText);
	}
	@Override
	public void updateContent(HasType<AbsentTeacherListType> content)
	{
		TeacherYourAbsent teacher = (TeacherYourAbsent) content;

		teacherNameText.setText(teacher.getName());
		blockImage.setText(teacher.block.letter);
		blockImage.setBackgroundFromBlockLetter(teacher.block.letter);
		timeText.setText(teacher.block.getTimeString());
	}
}
