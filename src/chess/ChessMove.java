package chess;

import player.Move;

public interface ChessMove extends Move<ChessPosition> {
    public Square getStart();

    public Square getEnd();

    public Delta getDelta();

    /**
     * Return the square that is occupied by the piece that's to be captured by this move.
     * Return null if no piece is to be captured.
     * Takes en passant into account.
     * @param board The board on which the move is to be made.
     * Requires that this move is sane.
     */
    public Square capturedSquare(ChessPosition board);

    /**
     * Return the en passant Square produced by this move, or null if there isn't one.
     * @param board The board on which the move is to be made.
     */
    public Square enPassantSquare(ChessPosition board);

    /**
     * Return whether this move is sane on a particular board.
     *
     * A move is sane if it is legal, IGNORING the no-check criterion.
     * That is, assert that the move is legal, except that the king might
     * be checked, or might have castled through or out of check.
     */
    public boolean isSane(ChessPosition board);

    /** Return whether a move is legal on a given board. */
    public boolean isLegal(ChessPosition board);

    /**
     * Return whether a move is open on a given board.
     * A move is open if the Squares between its start and end
     * are all unoccupied.
     *
     * Requires that the move is basic or diagonal: for other moves,
     * there is no definition of Squares "between" the start and end.
     */
    public boolean isOpen(ChessPosition board);

    /** Return whether this Move starts or ends at some Square. */
    public boolean startsOrEndsAt(Square square);

    /** Serialize this move as a 4-character String. */
    public String serialized();
}
