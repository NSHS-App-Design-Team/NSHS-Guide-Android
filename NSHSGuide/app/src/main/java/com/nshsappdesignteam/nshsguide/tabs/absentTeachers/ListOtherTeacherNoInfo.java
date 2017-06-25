package com.nshsappdesignteam.nshsguide.tabs.absentTeachers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;
import com.nshsappdesignteam.nshsguide.helper.BlockImage;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.HasType;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.OnVHClickListener;
import com.nshsappdesignteam.nshsguide.helper.recyclerview.VHClickUpdatable;

import java.util.ArrayList;
import java.util.List;

public class ListOtherTeacherNoInfo extends VHClickUpdatable<AbsentTeacherListType>
{
	private final TextView teacherNameText;
	private final List<BlockImage> blockImages;
	private final List<Block> blocks;

	public ListOtherTeacherNoInfo(View itemView, List<Block> blocks, OnVHClickListener parent, Context context)
	{
		super(itemView, parent);

		this.blocks = blocks;
		blockImages = new ArrayList<>(blocks.size());
		setUpBlockImages(context);
		teacherNameText = (TextView) itemView.findViewById(R.id.teacherText);
	}
	private void setUpBlockImages(Context context)
	{
		for (int i = 0; i < blocks.size(); i++)
		{
			Block block = blocks.get(i);

			int id = i + 1; //id is i+1 as id cannot be 0
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			BlockImage blockImage = (BlockImage) inflater.inflate(R.layout.list_small_block_image, (ViewGroup) itemView, false);
			blockImage.setId(id);
			blockImage.setText(block.letter);
			blockImage.setBackgroundFromBlockLetter(block.letter);
			blockImages.add(blockImage);
			((ViewGroup) itemView).addView(blockImage, blockImageLayoutParams(blockImage, id));
		}
	}
	private RelativeLayout.LayoutParams blockImageLayoutParams(BlockImage blockImage, int id)
	{
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) blockImage.getLayoutParams();
		layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.defaultTeacherImage);
		if (id == 1)
			layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.teacherText);
		else
			layoutParams.addRule(RelativeLayout.RIGHT_OF, id - 1);
		return layoutParams;
	}
	@Override
	public void updateContent(HasType<AbsentTeacherListType> content)
	{
		TeacherOtherAbsent teacher = (TeacherOtherAbsent) content;
		teacherNameText.setText(teacher.getName());

		for (int i = 0; i < blocks.size(); i++)
		{
			boolean isAbsent = teacher.absentForBlock.get(blocks.get(i).letter);
			blockImages.get(i).setFadeColor(!isAbsent);
		}
	}
}
