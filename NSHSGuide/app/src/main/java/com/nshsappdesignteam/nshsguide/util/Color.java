package com.nshsappdesignteam.nshsguide.util;

import com.nshsappdesignteam.nshsguide.R;
import com.nshsappdesignteam.nshsguide.helper.Block;

public final class Color
{
	public static int colorGivenLetter(String blockLetter)
	{
		if (blockLetter.equals(Block.BLOCK_LETTERS.get(0)))
			return R.color.red;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(1)))
			return R.color.pink;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(2)))
			return R.color.purple;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(3)))
			return R.color.blue;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(4)))
			return R.color.cyan;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(5)))
			return R.color.teal;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(6)))
			return R.color.lightGreen;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(7)))
			return R.color.amber;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(8)))
			return R.color.orange;

		return R.color.blueGrey;
	}
	public static int darkColorGivenLetter(String blockLetter)
	{
		if (blockLetter.equals(Block.BLOCK_LETTERS.get(0)))
			return R.color.redDark;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(1)))
			return R.color.pinkDark;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(2)))
			return R.color.purpleDark;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(3)))
			return R.color.blueDark;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(4)))
			return R.color.cyanDark;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(5)))
			return R.color.tealDark;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(6)))
			return R.color.lightGreenDark;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(7)))
			return R.color.amberDark;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(8)))
			return R.color.orangeDark;

		return R.color.blueGreyDark;
	}
	public static int bgGivenLetter(String blockLetter)
	{
		if (blockLetter.equals(Block.BLOCK_LETTERS.get(0)))
			return R.drawable.block_bg_a;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(1)))
			return R.drawable.block_bg_b;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(2)))
			return R.drawable.block_bg_c;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(3)))
			return R.drawable.block_bg_d;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(4)))
			return R.drawable.block_bg_e;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(5)))
			return R.drawable.block_bg_f;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(6)))
			return R.drawable.block_bg_g;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(7)))
			return R.drawable.block_bg_hr;
		else if (blockLetter.equals(Block.BLOCK_LETTERS.get(8)))
			return R.drawable.block_bg_j;

		return R.drawable.block_bg_default;
	}
}
