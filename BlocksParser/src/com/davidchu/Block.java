package com.davidchu;

public class Block
{
	public final String letter;
	public final int num;
	public final int length;
	public final Time startTime, endTime;
	public final boolean isLunchBlock;

	public Block(String letter, int number, Time startTime, int length, boolean isLunchBlock)
	{
		this.letter = letter;
		num = number;
		this.length = length;
		this.startTime = startTime;
		endTime = Time.add(startTime, length);
		this.isLunchBlock = isLunchBlock;
	}
	@Override
	public String toString()
	{
		return letter + num + ", starts " + startTime + ", ends " + endTime + ", lunch: " + isLunchBlock;
	}
}
