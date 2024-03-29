package com.stalepretzel.chess.exceptions;

/** Superclass of Exceptions related to the chess package.*/
public class ChessException extends Exception {

    private static final long serialVersionUID = 1L;

    public ChessException() {
        super();
    }

    public ChessException(String msg){
        super(msg);
    }

}
