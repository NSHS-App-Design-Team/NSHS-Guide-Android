package com.nshsappdesignteam.nshsguide.tabs.yourTeachers;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.BlockImage;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHLongClickListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHLongClickUpdatable;
import com.nshsappdesignteam.nshsguide.util.TeachersManager;

public class ListYourTeacherNoSubject extends VHLongClickUpdatable<YourTeacherListType>
{
	public final BlockImage blockImage;
	private final TextView teacherNameText;
	private final TextView lunchInfoText;
	private final TextView roomNumText;
	public final SparseArray<BlockImage> blockNumImageForNum = new SparseArray<>(4);

	public ListYourTeacherNoSubject(View itemView, OnVHLongClickListener parent)
	{
		super(itemView, parent);
		blockImage = (BlockImage) itemView.findViewById(R.id.blockImage);
		teacherNameText = (TextView) itemView.findViewById(R.id.teacherText);
		lunchInfoText = (TextView) itemView.findViewById(R.id.lunchText);
		roomNumText = (TextView) itemView.findViewById(R.id.roomNumText);
		BlockImage block1Image = (BlockImage) itemView.findViewById(R.id.block1Image);
		BlockImage block2Image = (BlockImage) itemView.findViewById(R.id.block2Image);
		BlockImage block3Image = (BlockImage) itemView.findViewById(R.id.block3Image);
		BlockImage block4Image = (BlockImage) itemView.findViewById(R.id.block4Image);
		blockNumImageForNum.append(1, block1Image);
		blockNumImageForNum.append(2, block2Image);
		blockNumImageForNum.append(3, block3Image);
		blockNumImageForNum.append(4, block4Image);
	}
	@Override
	public void updateContent(HasType<YourTeacherListType> content)
	{
		TeacherYours teacher = (TeacherYours) content;

		teacherNameText.setText(teacher.getName());
		blockImage.setText(teacher.blockLetter);
		blockImage.setBackgroundFromBlockLetter(teacher.blockLetter);
		for (int blockNum : Block.BLOCK_NUMS)
		{
			BlockImage blockImageForNum = blockNumImageForNum.get(blockNum);
			blockImageForNum.setBackgroundFromBlockLetter(teacher.blockLetter);
			//fade the BG of blocks the teacher doesn't teach
			blockImageForNum.setFadeColor(!teacher.blockNums.contains(blockNum));
		}

		showLunch(teacher.lunch);
		showRoomNum(teacher.roomNum);
	}
	private void showLunch(int lunchNum)
	{
		if (lunchNum == 0) {
			lunchInfoText.setVisibility(View.INVISIBLE);
			return;
		}

		lunchInfoText.setVisibility(View.VISIBLE);
		lunchInfoText.setText(TeachersManager.SINGLETON.LUNCHES.get(lunchNum - 1));
	}
	private void showRoomNum(String roomNum)
	{
		if (roomNum.isEmpty()) {
			roomNumText.setVisibility(View.INVISIBLE);
			return;
		}

		roomNumText.setVisibility(View.VISIBLE);
		roomNumText.setText(TeachersManager.SINGLETON.ROOM + " " + roomNum);
	}
}
