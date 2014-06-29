package main.java.chess;

import java.util.Collection;

import main.java.chess.piece.Piece;
import main.java.player.Position;

/**
 * A chess board at a specific position.
 * Implementations must be immutable.
 */
public interface ChessPosition extends Position<ChessPosition> {

    /** Get the piece at a square, or null if the square is empty. */
    public Piece getPiece(Square square);

    /** Get the en-passant square, or null if there isn't one.*/
    public Square getEnPassantSquare();

    public Piece.Color getToMoveColor();

    public CastlingInfo getCastlingInfo();

    /** Return whether the king of some color is in check. */
    public boolean checked(Piece.Color kingColor);

    // Make the return type more specific that is required
    // by Position<ChessPosition>.
    @Override
    public Collection<ChessMove> moves();

    /** Return whether any of certain Squares is occupied in this Position. */
    public boolean anyOccupied(Iterable<Square> squares);

}
