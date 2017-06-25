/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe.logic.steps;


import com.nshsappdesignteam.nshsguide.tictactoe.Coordinate;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.LogicStep;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.WinningCoordinate;

import java.util.Set;

/**
 *
 * @author 201328011
 */
public class WinningMove implements LogicStep
{

    @Override
    public Coordinate tryMove(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves) {
        //cannot possibly win with less than 4 moves on the board
        if (playerMoves.size() + machineMoves.size() < 4)
            return null;
        return new WinningCoordinate.Builder()
                .movesToWin(machineMoves)
                .movesToNotWin(playerMoves)
                .build()
                .coordinate();
    }
    
}
