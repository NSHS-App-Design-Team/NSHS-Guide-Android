package com.test.nshsmap;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

public class Route
{
	private ArrayList<Instruction> instructions;
	private HashSet<Intersection> usedIntersections;    //#s of intersections we already traveled through, to prevent infinite loops
	private int totalDistance = 0;

	public Route()
	{
		instructions = new ArrayList<>();
		usedIntersections = new HashSet<>();
	}
	//essentially copy a route (so you can extend it)
	public Route(Route route)
	{
		instructions = new ArrayList<>(route.instructions);
		usedIntersections = new HashSet<>(route.usedIntersections);
		totalDistance += route.totalDistance;
	}
	public void addInstruction(Instruction instruction)
	{
		instructions.add(instruction);
		totalDistance += instruction.distanceToIntersection;
	}
	public void addIntersection(Intersection intersection)
	{
		usedIntersections.add(intersection);
	}
	public boolean containsIntersection(Intersection intersection)
	{
		return usedIntersections.contains(intersection);
	}
	public int getTotalDistance()
	{
		return totalDistance;
	}
	public Instruction getFirstInstruction()
	{
		return instructions.get(0);
	}
	public Instruction getLastInstruction()
	{
		return instructions.get(instructions.size() - 1);
	}

	//translates absolute direction into relative direction (which way to turn when entering an intersection this way & exiting that way)
	//so if you enter intersection from "right" and exit in the "back" direction, you're essentially turning "left"
	private String directionToTurn(Direction enteredFrom, Direction going)
	{
		//find out which direction the user should turn
		Direction directionToTurnTo;

		switch (enteredFrom)
		{
			case Left:
				directionToTurnTo = going.rotate().rotate().rotate();
				break;
			case Right:
				directionToTurnTo = going.rotate();
				break;
			case Front:
				directionToTurnTo = going.rotate().rotate();
				break;
			case Back:
				directionToTurnTo = going;
				break;
			default:
				//should never run
				Log.i("Route", "weird enteredFrom: " + enteredFrom.toString());
				directionToTurnTo = going;
		}

		//find out what to display to the user
		switch (directionToTurnTo)
		{
			case Left:
			case Right:
				return "turn " + directionToTurnTo.toString();
			case Front:
				return "keep going forward";    //TODO in real life, just skip this intersection (but we don't have pictures, so we can't)
			default:
				//should never run
				return "WUT?";
		}
	}
	@Override
	public String toString()
	{
		String result = "";
		int i = 0;
		while (i + 1 < instructions.size())
		{
			Instruction instruction = instructions.get(i);
			Instruction nextInstruction = instructions.get(++i);
			result += "walk " + instruction.distanceToIntersection + "ft, then, at the next intersection, "
					+ directionToTurn(instruction.enteredIntersectionFrom, nextInstruction.directionToTurnTo) + "\n";
		}
		result += ", then walk " + instructions.get(instructions.size() - 1).distanceToIntersection + "ft";
		return result;
	}
}
