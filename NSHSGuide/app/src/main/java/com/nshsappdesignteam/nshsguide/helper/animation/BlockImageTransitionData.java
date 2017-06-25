package com.nshsappdesignteam.nshsguide.helper.animation;

import com.nshsappdesignteam.nshsguide.helper.BlockImage;

public class BlockImageTransitionData
{
	public final int paddingLeft;
	public final int paddingTop;
	public final int paddingRight;
	public final int paddingBottom;
	public final int width;
	public final int height;
	public final int gravity;
	public final int textColor;

	public BlockImageTransitionData(BlockImage blockImage)
	{
		this.paddingLeft = blockImage.getPaddingLeft();
		this.paddingTop = blockImage.getPaddingTop();
		this.paddingRight = blockImage.getPaddingRight();
		this.paddingBottom = blockImage.getPaddingBottom();
		this.width = blockImage.getWidth();
		this.height = blockImage.getHeight();
		this.gravity = blockImage.getGravity();
		this.textColor = blockImage.getCurrentTextColor();
	}
}
