package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHClickListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.InfoOfType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHClickUpdatable;

public class ListAnnouncement extends VHClickUpdatable<AbsentTeacherListType>
{
	private final TextView announcementText;

	public ListAnnouncement(View itemView, OnVHClickListener parent)
	{
		super(itemView, parent);
		announcementText = (TextView) itemView.findViewById(R.id.someText);
		setUpAnnouncement();
	}
	private void setUpAnnouncement()
	{
		announcementText.setBackgroundResource(R.color.accent);
		announcementText.setGravity(Gravity.CENTER);
		announcementText.setTextColor(Color.WHITE);
		announcementText.setSingleLine();
	}
	@Override
	public void updateContent(HasType<AbsentTeacherListType> content)
	{
		InfoOfType infoOfType = (InfoOfType) content;
		announcementText.setText(infoOfType.getInfo());
	}
}
