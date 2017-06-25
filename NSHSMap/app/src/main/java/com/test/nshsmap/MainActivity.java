package com.test.nshsmap;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends ActionBarActivity implements TextWatcher
{
	HashMap<Integer, Room> roomForNum = new HashMap<>();
	HashMap<Integer, Intersection> intersectionForNum = new HashMap<>();
	EditText currentRoomNumText;
	EditText destinationRoomNumText;
	TextView infoText;
	boolean calculating = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		View toolbarCustomView = LayoutInflater.from(this).inflate(R.layout.toolbar_view, toolbar);
		TextView title = (TextView) toolbarCustomView.findViewById(R.id.title);
		title.setText(getTitle());

		infoText = (TextView) findViewById(R.id.searchDisplay);
		currentRoomNumText = (EditText) toolbarCustomView.findViewById(R.id.currentRoomNum);
		destinationRoomNumText = (EditText) toolbarCustomView.findViewById(R.id.destinationRoomNum);
		currentRoomNumText.addTextChangedListener(this);
		destinationRoomNumText.addTextChangedListener(this);

		loadData();
	}
	private void loadData()
	{
		for (int i = 0; i < 7; i++)
		{
			intersectionForNum.put(410 + i, new Intersection(1, 410 + i));
		}
		intersectionForNum.put(310, new Intersection(1, 310));
		//TODO add intersection info for staircases & elevators & exits once classes are built
		intersectionForNum.get(411).addClosebyIntersection(intersectionForNum.get(412), 15, Direction.Front);
		intersectionForNum.get(412).addClosebyIntersection(intersectionForNum.get(411), 15, Direction.Back);
		intersectionForNum.get(412).addClosebyIntersection(intersectionForNum.get(414), 65, Direction.Left);
		intersectionForNum.get(414).addClosebyIntersection(intersectionForNum.get(412), 65, Direction.Right);
		intersectionForNum.get(414).addClosebyIntersection(intersectionForNum.get(310), 94, Direction.Left);
		intersectionForNum.get(310).addClosebyIntersection(intersectionForNum.get(414), 94, Direction.Right);

		//TODO add bathrooms & water fountains & things with names once classes are built
		roomForNum.put(4119, new Room(1));
		roomForNum.put(4116, new Room(1));
		roomForNum.put(4115, new Room(1));
		roomForNum.put(4132, new Room(1));
		roomForNum.put(4105, new Room(1));
		roomForNum.put(4114, new Room(1));
		roomForNum.put(4110, new Room(1));
		roomForNum.put(4103, new Room(1));
		roomForNum.put(4101, new Room(1));
		roomForNum.put(3102, new Room(1));
		roomForNum.put(3101, new Room(1));
		roomForNum.put(3104, new Room(1));
		roomForNum.put(3107, new Room(1));
		roomForNum.get(4119).addClosebyIntersection(intersectionForNum.get(411), 104, Direction.Front, Direction.Back);
		roomForNum.get(4116).addClosebyIntersection(intersectionForNum.get(411), 89, Direction.Left, Direction.Back);
		roomForNum.get(4115).addClosebyIntersection(intersectionForNum.get(411), 81, Direction.Right, Direction.Back);
		roomForNum.get(4132).addClosebyIntersection(intersectionForNum.get(411), 46, Direction.Left, Direction.Back);
		roomForNum.get(4105).addClosebyIntersection(intersectionForNum.get(411), 12, Direction.Right, Direction.Back);
		roomForNum.get(4110).addClosebyIntersection(intersectionForNum.get(411), 4, Direction.Right, Direction.Front);
		roomForNum.get(4110).addClosebyIntersection(intersectionForNum.get(412), 13, Direction.Left, Direction.Back);
		roomForNum.get(4103).addClosebyIntersection(intersectionForNum.get(411), 20, Direction.Left, Direction.Front);
		roomForNum.get(4103).addClosebyIntersection(intersectionForNum.get(412), 3, Direction.Left, Direction.Front);
		roomForNum.get(4101).addClosebyIntersection(intersectionForNum.get(412), 5, Direction.Front, Direction.Front);
		roomForNum.get(4101).addClosebyIntersection(intersectionForNum.get(414), 70, Direction.Right, Direction.Right);
		roomForNum.get(3101).addClosebyIntersection(intersectionForNum.get(414), 29, Direction.Left, Direction.Left);
		roomForNum.get(3101).addClosebyIntersection(intersectionForNum.get(310), 64, Direction.Right, Direction.Right);
		roomForNum.get(3102).addClosebyIntersection(intersectionForNum.get(414), 27, Direction.Right, Direction.Left);
		roomForNum.get(3102).addClosebyIntersection(intersectionForNum.get(310), 66, Direction.Left, Direction.Right);
		roomForNum.get(3107).addClosebyIntersection(intersectionForNum.get(414), 82, Direction.Left, Direction.Left);
		roomForNum.get(3107).addClosebyIntersection(intersectionForNum.get(310), 11, Direction.Right, Direction.Right);
		roomForNum.get(3104).addClosebyIntersection(intersectionForNum.get(414), 78, Direction.Right, Direction.Left);
		roomForNum.get(3104).addClosebyIntersection(intersectionForNum.get(310), 15, Direction.Left, Direction.Right);
	}
	private void calculate()
	{
		int currentRoomNum = Integer.parseInt(currentRoomNumText.getText().toString());
		int destinationRoomNum = Integer.parseInt(destinationRoomNumText.getText().toString());
		HashMap<Intersection, Instruction> currentRoomInstructions = roomForNum.get(currentRoomNum).getInstructions();
		HashMap<Intersection, Instruction> destinationRoomInstructions = roomForNum.get(destinationRoomNum).getInstructions();

		//check if on the same hallway (doesn't need to go through an intersection)
		for (Intersection currentRoomIntersection : currentRoomInstructions.keySet())
		{
			//connected by at most 1 intersection
			if (destinationRoomInstructions.containsKey(currentRoomIntersection))
			{
				Instruction currentRoomInstruction = currentRoomInstructions.get(currentRoomIntersection);
				Instruction destinationRoomInstruction = destinationRoomInstructions.get(currentRoomIntersection);

				//entered from same direction, same hallway
				if (currentRoomInstruction.enteredIntersectionFrom == destinationRoomInstruction.enteredIntersectionFrom)
				{
					int overlap = currentRoomInstruction.distanceToIntersection - destinationRoomInstruction.distanceToIntersection;
					//reverse directions if necessary, based on rooms' relative locations to each other
					if (overlap > 0)
					{
						//use original directions to find how to get to the other room
						infoText.setText("Stand at the door of room " + currentRoomNum
								+ "\n" + "Turn " + currentRoomInstruction.directionToTurnTo + ", walk " + overlap + "ft, then turn "
								+ destinationRoomInstruction.directionToTurnTo + ", arriving at room " + destinationRoomNum);
					}
					else
					{
						//use reverse directions to find how to get to the other room
						infoText.setText("Stand at the door of room " + currentRoomNum
								+ "\n" + "Turn " + currentRoomInstruction.directionToTurnTo.reverse() + ", walk " + -overlap + "ft, then turn "
								+ destinationRoomInstruction.directionToTurnTo.reverse() + ", arriving at room " + destinationRoomNum);
					}
					return;
				}
			}
		}

		Route shortestRoute = new Route();
		Set<Intersection> destinationIntersections = destinationRoomInstructions.keySet();

		for (Map.Entry<Intersection, Instruction> entry : currentRoomInstructions.entrySet())
		{
			ArrayList<Route> tempRoutes = new ArrayList<>();

			Route initialRoute = new Route();
			Instruction roomInstructions = entry.getValue();
			initialRoute.addInstruction(new Instruction(entry.getKey(), roomInstructions.distanceToIntersection, roomInstructions.enteredIntersectionFrom.reverse()));
			initialRoute.addIntersection(entry.getKey());
			tempRoutes.add(initialRoute);

			//all intersections directly next to the destination
			for (Intersection destinationIntersection : destinationIntersections)
			{
				//all routes possible (size expands WITHIN the loop)
				for (int i = 0; i < tempRoutes.size(); i++)
				{
					Route route = tempRoutes.get(i);

					//if we have not arrived at the destination yet (for this route), keep branching/moving toward destination
					if (!route.containsIntersection(destinationIntersection))
					{
						extendAndBranchRoute(route, tempRoutes);
						Log.i("Branching", "temp route size: " + tempRoutes.size());
					}
					else
					{
						//add final instructions to get to specific room
						Instruction destinationInstruction = destinationRoomInstructions.get(destinationIntersection);
						route.addInstruction(new Instruction(destinationInstruction.intersection, destinationInstruction.distanceToIntersection, destinationInstruction.enteredIntersectionFrom));

						//check to see if this is the shortest (equals 0 when first initiated)
						if (shortestRoute.getTotalDistance() == 0 || route.getTotalDistance() < shortestRoute.getTotalDistance())
						{
							shortestRoute = route;
							Log.i("New shortest route", "route size: " + shortestRoute.getTotalDistance());
						}
					}
				}
			}
		}

		infoText.setText("Stand at the door of room " + currentRoomNum
				+ "\n" + "Turn " + currentRoomInstructions.get(shortestRoute.getFirstInstruction().intersection).directionToTurnTo
				+ ", " + shortestRoute.toString() + "\nand turn "
				+ destinationRoomInstructions.get(shortestRoute.getLastInstruction().intersection).directionToTurnTo.reverse()
				+ ", arriving at room " + destinationRoomNum);
	}
	private void extendAndBranchRoute(Route route, ArrayList<Route> tempRoutes)
	{
		Intersection currentIntersection = route.getLastInstruction().intersection;
		for (Map.Entry<Intersection, Instruction> nextInstruction : currentIntersection.getInstructions().entrySet())
		{
			//check if you've been down this path before
			if (!route.containsIntersection(nextInstruction.getKey()))
			{
				Route branch = new Route(route);
				branch.addInstruction(nextInstruction.getValue());
				branch.addIntersection(nextInstruction.getKey());
				tempRoutes.add(branch);
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{

	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{

	}
	@Override
	public void afterTextChanged(Editable s)
	{
		//if we're ready to go
		if (currentRoomNumText.getText().length() == 4 && destinationRoomNumText.getText().length() == 4)
		{
			if (!calculating)
			{
				calculating = true;
				calculate();
				calculating = false;
			}
		}
	}
}
