package com.nshsappdesignteam.nshsguide.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.util.Color;

public class BlockImage extends TextView
{
	private static final int[] FADE_COLOR = new int[]{R.attr.fade_color};
	private boolean fadeColor = false;

	public BlockImage(Context context)
	{
		this(context, null);
	}
	public BlockImage(Context context, AttributeSet attrs)
	{
		super(context, attrs, R.attr.blockImageStyle);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.block_image, 0, 0);
		setFadeColor(typedArray.getBoolean(R.styleable.block_image_fade_color, false));
		typedArray.recycle();
	}
	public void setFadeColor(boolean fadeColor)
	{
		this.fadeColor = fadeColor;
		refreshDrawableState();
	}
	@Override
	protected int[] onCreateDrawableState(int extraSpace)
	{
		int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (fadeColor)
		{
			mergeDrawableStates(drawableState, FADE_COLOR);
		}
		return drawableState;
	}
	public void setBackgroundFromBlockLetter(String blockLetter)
	{
		setBackgroundResource(Color.bgGivenLetter(blockLetter));
	}
}
