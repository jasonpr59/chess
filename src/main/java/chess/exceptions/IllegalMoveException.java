package main.java.chess.exceptions;

/** Indicates that a ChessMove cannot legally be made from a ChessPosition. */
public class IllegalMoveException extends ChessException {

    private static final long serialVersionUID = 1L;

    public IllegalMoveException() {
        super();
    }

    public IllegalMoveException(String msg) {
        super(msg);
    }
}
