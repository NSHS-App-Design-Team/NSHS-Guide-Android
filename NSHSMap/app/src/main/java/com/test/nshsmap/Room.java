package com.test.nshsmap;

import java.util.HashMap;

public class Room
{
	public final int floor;
	private HashMap<Intersection, Instruction> instructions = new HashMap<>();

	public Room(int floor)
	{
		this.floor = floor;
	}
	public void addClosebyIntersection(Intersection inter, int distance, Direction directionToTurnTo, Direction enteredIntersectionFrom)
	{
		instructions.put(inter, new Instruction(inter, distance, directionToTurnTo, enteredIntersectionFrom));
	}
	public HashMap<Intersection, Instruction> getInstructions()
	{
		return new HashMap<>(instructions);
	}
}
