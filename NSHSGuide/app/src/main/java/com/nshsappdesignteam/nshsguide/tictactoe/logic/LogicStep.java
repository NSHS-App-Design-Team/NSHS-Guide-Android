/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe.logic;

import com.nshsappdesignteam.nshsguide.tictactoe.Coordinate;

import java.util.Set;
/**
 *
 * @author 201328011
 */
public interface LogicStep {
    Coordinate tryMove(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves);
    
    
}
