package com.test.nshsmap;

import java.util.HashMap;

public class Intersection
{
	public final int floor;
	public final int num;
	private HashMap<Intersection, Instruction> instructions = new HashMap<>();

	public Intersection(int floor, int num)
	{
		this.floor = floor;
		this.num = num;
	}
	//direction is absolute here, with Front = front entrance of school
	public void addClosebyIntersection(Intersection inter, int distance, Direction directionToTurnTo)
	{
		instructions.put(inter, new Instruction(inter, distance, directionToTurnTo));
	}
	public HashMap<Intersection, Instruction> getInstructions()
	{
		return new HashMap<>(instructions);
	}
}
