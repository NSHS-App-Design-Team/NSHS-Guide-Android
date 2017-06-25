package com.nshsappdesignteam.nshsguide.tictactoe;

public class Coordinate
{
	public final int row;
	public final int column;

	public Coordinate(int row, int column)
	{
		this.row = row;
		this.column = column;
	}
	@Override
	public int hashCode()
	{
		return row + 31 * column;
	}
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o instanceof Coordinate)
		{
			Coordinate c = (Coordinate) o;
			return c.row == this.row && c.column == this.column;
		}
		return false;
	}
	@Override
	public String toString()
	{
		return "row: " + row + ", column: " + column;
	}
}
