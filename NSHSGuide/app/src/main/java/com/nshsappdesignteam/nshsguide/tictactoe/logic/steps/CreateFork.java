/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe.logic.steps;

import com.nshsappdesignteam.nshsguide.tictactoe.Coordinate;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.ForkCoordinate;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.LogicStep;

import java.util.Set;
/**
 *
 * @author David
 */
public class CreateFork implements LogicStep
{

    @Override
    public Coordinate tryMove(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves) {
        return new ForkCoordinate.Builder()
                .movesToFork(machineMoves)
                .movesToPreventFork(playerMoves)
                .build()
                .createFork();
    }
    
}
