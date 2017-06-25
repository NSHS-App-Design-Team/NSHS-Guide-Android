/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nshsappdesignteam.nshsguide.tictactoe;

/**
 *
 * @author 201328011
 */
public enum Symbol {
    X, O, EMPTY;
    
    public static String toString(Symbol symbol)
    {
        if (symbol == O)
            return "o";
        else if (symbol == X)
            return "x";
        else
            return "-";
    }
    public static Symbol oppositeSymbol(Symbol symbol)
    {
        if (symbol == O)
            return X;
        else if (symbol == X)
            return O;
        else
            return EMPTY;
    }
}
