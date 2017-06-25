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
 * @author 201328011
 */
public class RandomMove implements LogicStep
{

    @Override
    public Coordinate tryMove(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves)
    {
        Set<Coordinate> validMoves = ValidMoves.SINGLETON.any(playerMoves, machineMoves);
        //this isn't actually random...
        if (validMoves.isEmpty())
            return null;
        return validMoves.iterator().next();
    }
    
}
