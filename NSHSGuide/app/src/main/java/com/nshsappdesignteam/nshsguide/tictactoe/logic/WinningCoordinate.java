/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe.logic;

import com.nshsappdesignteam.nshsguide.tictactoe.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/**
 *
 * @author David
 */
public class WinningCoordinate
{
    private final Set<Coordinate> movesToNotWin;
    private final Coordinate ignorePiece;
    private final Set<Coordinate> validMoves;
    
    public static class Builder {
        //default values (use emptySet instead of null so operationsn won't result in crash)
        private Set<Coordinate> movesToWin = Collections.emptySet();
        private Set<Coordinate> movesToNotWin = Collections.emptySet();
        private Coordinate ignorePiece = null;
        public Builder movesToWin(Set<Coordinate> movesToWin) {
            this.movesToWin = movesToWin;
            return this;
        }
        public Builder movesToNotWin(Set<Coordinate> movesToNotWin) {
            this.movesToNotWin = movesToNotWin;
            return this;
        }
        public Builder ignorePiece(Coordinate ignorePiece) {
            this.ignorePiece = ignorePiece;
            return this;
        }
        public WinningCoordinate build() {
            return new WinningCoordinate(movesToWin, movesToNotWin, ignorePiece);
        }
    }
    //private constructor forces user to use Builder class
    private WinningCoordinate(Set<Coordinate> movesToWin, Set<Coordinate> movesToNotWin, Coordinate ignorePiece) {
        this.movesToNotWin = movesToNotWin;
        this.ignorePiece = ignorePiece;
        validMoves = ValidMoves.SINGLETON.any(movesToWin, movesToNotWin);
    }
    public Coordinate coordinate() {
        Coordinate winningMove = horizontalWinningMove();
        if (winningMove != null)
            return winningMove;
        winningMove = verticalWinningMove();
        if (winningMove != null)
            return winningMove;
        winningMove = diagonalWinningMove();
        return winningMove;
    }
    private Coordinate horizontalWinningMove() {
        for (int row = 0; row <= 2; row++) {
            List<Coordinate> validMovesInRow = new ArrayList();
            for (int col = 0; col <= 2; col++) {
                Coordinate c = new Coordinate(row, col);

                if (movesToNotWin.contains(c)) {
                    validMovesInRow.clear();
                    break;
                }

                if (validMoves.contains(c))
                    validMovesInRow.add(c);
            }
            if (validMovesInRow.size() == 1)
                if (!validMovesInRow.get(0).equals(ignorePiece))
                    return validMovesInRow.get(0);
        }
        return null;
    }
    private Coordinate verticalWinningMove() {
        for (int col = 0; col <= 2; col++) {
            List<Coordinate> validMovesInCol = new ArrayList();
            for (int row = 0; row <= 2; row++) {
                Coordinate c = new Coordinate(row, col);
                if (movesToNotWin.contains(c)) {
                    validMovesInCol.clear();
                    break;
                }
                if (validMoves.contains(c))
                    validMovesInCol.add(c);
            }
            if (validMovesInCol.size() == 1)
                if (!validMovesInCol.get(0).equals(ignorePiece))
                    return validMovesInCol.get(0);
        }
        return null;
    }
    private Coordinate diagonalWinningMove() {
        List<Coordinate> validMovesInA = new ArrayList();
        List<Coordinate> validMovesInB = new ArrayList();
        for (int counter = 0; counter <= 2; counter++) {
            Coordinate a = new Coordinate(counter, counter);
            Coordinate b = new Coordinate(2 - counter, counter);

            //make diagonal size > 2 so it isn't considered
            if (movesToNotWin.contains(a)) {
                validMovesInA.add(a);
                validMovesInA.add(a);
            }
            if (movesToNotWin.contains(b)) {
                validMovesInB.add(b);
                validMovesInB.add(b);
            }

            if (validMoves.contains(a)) {
                validMovesInA.add(a);
            }
            if (validMoves.contains(b)) {
                validMovesInB.add(b);
            }
        }
        if (validMovesInA.size() == 1)
            if (!validMovesInA.get(0).equals(ignorePiece))
                return validMovesInA.get(0);
        if (validMovesInB.size() == 1)
            if (!validMovesInB.get(0).equals(ignorePiece))
                return validMovesInB.get(0);

        return null;
    }
}
