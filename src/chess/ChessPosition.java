package chess;

import player.Position;

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

    /**
     * Return whether the given square is currently under attack.
     * A square is under attack if a piece of the toMoveColor is
     * attacking it.
     * TODO: Consider allowing caller to specify attackerColor.
     */
    public boolean isAttackable(Square kingSquare);

    /** Return whether the king of some color is in check. */
    public boolean checked(Piece.Color kingColor);

    /** Get the set of sane moves available to the piece on a square. */
    public Iterable<ChessMove> saneMoves(Square start);
}
