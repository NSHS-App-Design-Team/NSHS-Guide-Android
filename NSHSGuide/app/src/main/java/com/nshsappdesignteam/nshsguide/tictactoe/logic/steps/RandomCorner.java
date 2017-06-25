/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe.logic.steps;

import com.nshsappdesignteam.nshsguide.tictactoe.Coordinate;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.LogicStep;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.ValidMoves;

import java.util.Set;

/**
 *
 * @author David
 */
public class RandomCorner implements LogicStep
{

    @Override
    public Coordinate tryMove(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves) {
        Set<Coordinate> validCorners = ValidMoves.SINGLETON.corners(playerMoves, machineMoves);
        if (validCorners.isEmpty())
            return null;
        return validCorners.iterator().next();
    }
    
}
