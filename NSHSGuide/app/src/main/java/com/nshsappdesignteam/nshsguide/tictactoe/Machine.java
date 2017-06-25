/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.nshsappdesignteam.nshsguide.tictactoe;

import com.nshsappdesignteam.nshsguide.tictactoe.logic.LogicStep;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.steps.CenterMove;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.steps.CreateFork;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.steps.PreventFork;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.steps.PreventLosingMove;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.steps.RandomCorner;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.steps.RandomMove;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.steps.WinningMove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Machine
{
	private final TicTacToeActivity view;
	private final Symbol playerSymbol;
	private final Set<Coordinate> playerMoves = new HashSet<>(5);
	private final Set<Coordinate> machineMoves = new HashSet<>(5);
	private final List<LogicStep> logicSteps = new ArrayList<>();
	private boolean machineWon = false;

	public Machine(Symbol playerSymbol, TicTacToeActivity view)
	{
		this.playerSymbol = playerSymbol;
		this.view = view;

		addLogicSteps();
	}
	private void addLogicSteps()
	{
		logicSteps.add(new PreventLosingMove());
		logicSteps.add(new CenterMove());
		logicSteps.add(new CreateFork());
		logicSteps.add(new PreventFork());
		logicSteps.add(new RandomCorner());
		logicSteps.add(new RandomMove());
	}
	public boolean putMoveAsPlayer(Coordinate coordinate)
	{
		if (!moveValid(coordinate))
			return false;
		playerMoves.add(coordinate);
		return true;
	}
	public boolean putMoveAsMachine(Coordinate coordinate)
	{
		if (!moveValid(coordinate))
			return false;
		machineMoves.add(coordinate);
		view.showMachineMove(coordinate);
		return true;
	}
	private boolean moveValid(Coordinate coordinate)
	{
		return !playerMoves.contains(coordinate) && !machineMoves.contains(coordinate);
	}
	public void tryMoves()
	{
		Coordinate c = new WinningMove().tryMove(playerMoves, machineMoves);

		if (c != null)
		{
			putMoveAsMachine(c);
			machineWon = true;
			return;
		}
		Random random = new Random();
		if (random.nextDouble() > 0.8) {
			c = new RandomMove().tryMove(playerMoves, machineMoves);
			putMoveAsMachine(c);
			return;
		}
		for (LogicStep logicStep : logicSteps)
		{
			c = logicStep.tryMove(playerMoves, machineMoves);

			if (c != null)
			{
				putMoveAsMachine(c);
				return;
			}

		}
	}
	public boolean isGameOver()
	{
		//we're assuming the machine cannot lose
		return playerMoves.size() + machineMoves.size() == 9 || machineWon;
	}
	public boolean hasMachineWon()
	{
		return machineWon;
	}
}