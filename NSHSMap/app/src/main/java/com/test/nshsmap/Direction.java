package com.test.nshsmap;

import android.util.Log;

enum Direction
{
	Left, Right, Front, Back, Upstairs, Downstairs;

	@Override
	public String toString()
	{
		switch (this)
		{
			//TODO localize in strings.xml
			case Left:
				return "Left";
			case Right:
				return "Right";
			case Front:
				return "Front";
			case Back:
				return "Back";
			case Upstairs:
				return "Upstairs";
			case Downstairs:
				return "Downstairs";
			default:
				return "The fudge?";
		}
	}
	public Direction reverse()
	{
		switch (this)
		{
			case Left:
				return Direction.Right;
			case Right:
				return Direction.Left;
			case Front:
				return Direction.Back;
			case Back:
				return Direction.Front;
			default:
				//should never run
				Log.e("Instruction", "weird direction given");
				return Direction.Right;
		}
	}
	public Direction rotate()   //rotates CLOCKWISE (bearings)
	{
		switch (this)
		{
			case Left:
				return Direction.Front;
			case Right:
				return Direction.Back;
			case Front:
				return Direction.Right;
			case Back:
				return Direction.Left;
			default:
				//should never run
				Log.e("Instruction", "weird direction given");
				return Direction.Right;
		}
	}
}
