/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe.logic;

import com.nshsappdesignteam.nshsguide.tictactoe.Coordinate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author David
 */
public class ForkCoordinate
{
    private final Set<Coordinate> movesToFork;
    private final Set<Coordinate> movesToPreventFork;
    private final Set<Coordinate> validMoves;
    
    public static class Builder {
        //default values (use emptySet instead of null so operationsn won't result in crash)
        private Set<Coordinate> movesToFork = Collections.emptySet();
        private Set<Coordinate> movesToPreventFork = Collections.emptySet();

        public Builder movesToFork(Set<Coordinate> movesToFork) {
            this.movesToFork = movesToFork;
            return this;
        }
        public Builder movesToPreventFork(Set<Coordinate> movesToPreventFork) {
            this.movesToPreventFork = movesToPreventFork;
            return this;
        }
        public ForkCoordinate build() {
            return new ForkCoordinate(movesToPreventFork, movesToFork);
        }
    }
    //private constructor forces user to use Builder class
    private ForkCoordinate(Set<Coordinate> movesToPreventFork, Set<Coordinate> movesToFork) {
        this.movesToPreventFork = movesToPreventFork;
        this.movesToFork = movesToFork;
        validMoves = ValidMoves.SINGLETON.any(movesToPreventFork, movesToFork);
    }
    public Coordinate preventFork() {
        for (Coordinate futureMoveToFork : validMoves) {
            if (!canForkWithCoordinate(futureMoveToFork))
                 continue;
            
            return coordinateToPreventFork();
        }
        return null;
    }
    public Coordinate createFork() {
        for (Coordinate futureMoveToFork : validMoves)
            if (canForkWithCoordinate(futureMoveToFork))
                 return futureMoveToFork;
        return null;
    }
    private Coordinate coordinateToPreventFork() {
        for (Coordinate moveToPreventFork : validMoves) {
            Set<Coordinate> futureMovesToPreventFork = new HashSet<>(movesToPreventFork);
            futureMovesToPreventFork.add(moveToPreventFork);
            Coordinate winningMove = new WinningCoordinate.Builder().movesToWin(futureMovesToPreventFork)
                    .movesToNotWin(movesToFork)
                    .build()
                    .coordinate();

            if (winningMove == null)
                continue;
            //see if opponent can fork when they are preventing us from winning
            if (canForkWithCoordinate(winningMove))
                continue;
            return moveToPreventFork;
        }
        System.out.println("hell naw (coordinateToPreventFork failed)");
        return null;
    }
    private boolean canForkWithCoordinate(Coordinate coordinate) {
        Set<Coordinate> futureMovesToFork = new HashSet<>(movesToFork);
        futureMovesToFork.add(coordinate);
        Coordinate coordinateToWin = new WinningCoordinate.Builder().movesToWin(futureMovesToFork)
                .movesToNotWin(movesToPreventFork)
                .build()
                .coordinate();
        if (coordinateToWin == null)
            return false;

        Coordinate coordinateToWin2 = new WinningCoordinate.Builder().movesToWin(futureMovesToFork)
                .movesToNotWin(movesToPreventFork)
                .ignorePiece(coordinateToWin)
                .build()
                .coordinate();
        if (coordinateToWin2 == null)
            return false;
        
        //two ways to win, can fork
        return true;
    }
}
