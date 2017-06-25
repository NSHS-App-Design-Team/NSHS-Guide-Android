package com.test.nshsmap;

public class Instruction
{
	public final Intersection intersection; //the NEXT intersection
	public final int distanceToIntersection;
	public final Direction directionToTurnTo, enteredIntersectionFrom;

	//used by rooms, 1st direction: hallway's position relative to room; 2nd: where you entered the intersection in absolute direction
	public Instruction(Intersection inter, int distance, Direction directionToTurnTo, Direction enteredIntersectionFrom)
	{
		intersection = inter;
		distanceToIntersection = distance;
		this.directionToTurnTo = directionToTurnTo;
		this.enteredIntersectionFrom = enteredIntersectionFrom;
	}
	//used by intersections, enteredDirection = reverse of turned direction (if I walk left to you, then I'd be walking to your right)
	public Instruction(Intersection inter, int distance, Direction directionToTurnTo)
	{
		intersection = inter;
		distanceToIntersection = distance;
		this.directionToTurnTo = directionToTurnTo;
		enteredIntersectionFrom = directionToTurnTo.reverse();
	}
}
