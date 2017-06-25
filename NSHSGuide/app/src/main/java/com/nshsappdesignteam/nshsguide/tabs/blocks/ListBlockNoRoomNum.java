package com.nshsappdesignteam.nshsguide.tabs.blocks;

import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.BlockImage;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;

public class ListBlockNoRoomNum extends BlocksUpdatableVH
{
	private final BlockImage blockImage;
	private final TextView teacherText;
	private final TextView timeText;

	public ListBlockNoRoomNum(View itemView)
	{
		super(itemView);
		blockImage = (BlockImage) itemView.findViewById(R.id.blockImage);
		teacherText = (TextView) itemView.findViewById(R.id.teacherText);
		timeText = (TextView) itemView.findViewById(R.id.timeText);
	}
	@Override
	public void updateContent(HasType<BlocksListType> content)
	{
		TeacherWithBlock teacherWithBlock = (TeacherWithBlock) content;
		blockImage.setText(teacherWithBlock.block.getBlockWithNumSubscript());
		blockImage.setBackgroundFromBlockLetter(teacherWithBlock.block.letter);
		timeText.setText(teacherWithBlock.block.getTimeString());

		String teacherNameOrSubject = teacherWithBlock.teacher.getNameOrSubject();
		teacherText.setText(teacherNameOrSubject);
		if (teacherNameOrSubject.equals(TeachersManager.SINGLETON.FREE_BLOCK) || teacherNameOrSubject.equals(TeachersManager.SINGLETON.CANCELLED_CLASS))
			itemView.setBackgroundResource(R.color.blueGreyLight);
		else
			itemView.setBackgroundResource(0);  //remove bg
	}
}
