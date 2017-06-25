/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe.logic;

import com.nshsappdesignteam.nshsguide.tictactoe.Coordinate;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author David
 */
public class ValidMoves
{
    private static final Set<Coordinate> allMoves = new HashSet<>(9);
    public static final ValidMoves SINGLETON = new ValidMoves();
    
    //private constructor to enforce Singleton pattern
    private ValidMoves() {
        for (int row = 0; row <= 2; row++)
            for (int col = 0; col <= 2; col++)
                allMoves.add(new Coordinate(row, col));
    }

    //order of sets given in parameter is ambiguous but also unimportant
    public Set<Coordinate> any(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves)
    {
        Set<Coordinate> validMoves = new HashSet<>();
        for (Coordinate move : allMoves)
            if (!playerMoves.contains(move) && !machineMoves.contains(move))
                validMoves.add(move);
        return validMoves;
    }
    public Set<Coordinate> edges(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves)
    {
        Set<Coordinate> validMoves = any(playerMoves, machineMoves);
        Set<Coordinate> validEdges = new HashSet<>();
        for (Coordinate move : validMoves)
            if (move.row == 1 ^ move.column == 1)   //exclusive OR to avoid center
                validEdges.add(move);
        return validEdges;
    }
    public Set<Coordinate> corners(Set<Coordinate> playerMoves, Set<Coordinate> machineMoves)
    {
        Set<Coordinate> allValidMoves = any(playerMoves, machineMoves);
        Set<Coordinate> validCorners = new HashSet<>();
        for (Coordinate move : allValidMoves)
            if ((move.row == 2 || move.row == 0) && (move.column == 2 || move.column == 0)) //any piece in a corner has 0 or 2 as row & col
                validCorners.add(move);
        return validCorners;
    }
}
