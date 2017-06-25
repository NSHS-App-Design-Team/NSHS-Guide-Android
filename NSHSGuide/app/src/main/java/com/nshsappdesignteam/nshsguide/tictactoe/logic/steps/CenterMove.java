/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe.logic.steps;


import com.nshsappdesignteam.nshsguide.tictactoe.Coordinate;
import com.nshsappdesignteam.nshsguide.tictactoe.logic.LogicStep;

import java.util.Set;

/**
 *
 * @author 200716332
 */
public class CenterMove implements LogicStep
{

    @Override
    public Coordinate tryMove(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves) {
        Coordinate center = new Coordinate(1, 1);
        if (playerMoves.contains(center) || machineMoves.contains(center))
            return null;
        return center;
    }
    
}
