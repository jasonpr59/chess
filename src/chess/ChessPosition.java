package chess;

import java.util.Collection;

import player.Position;

/**
 * A chess board at a specific position.
 * Implementations must be immutable.
 */
public interface ChessPosition extends Position<ChessPosition> {

    /** Get the piece at a square, or null if the square is empty. */
    public Piece getPiece(Square square);

    /** Return whether a square is empty. */
    public boolean isEmpty(Square square);

    /** Get the en-passant square, or null if there isn't one.*/
    public Square getEnPassantSquare();

    public Piece.Color getToMoveColor();

    public CastlingInfo getCastlingInfo();

    /** Return the piece that would move if this move were performed. */
    public Piece movingPiece(ChessMove move);

    /**
     * Return whether the given square is currently under attack.
     * A square is under attack if a piece of the toMoveColor is
     * attacking it.
     * TODO: Consider allowing caller to specify attackerColor.
     */
    public boolean isAttackable(Square target);

    /** Return the square that the king of some color occupies. */
    public Square kingSquare(Piece.Color kingColor);

    /** Return whether the king of some color is in check. */
    public boolean checked(Piece.Color kingColor);

    /** Get the set of sane moves available to the piece on a square. */
    public Iterable<ChessMove> saneMoves(Square start);

    /** Return the subset of sane moves from a set of moves. */
    public Collection<ChessMove> filterSane(Collection<ChessMove> candidates);

    /** Return whether the king is unmoved and the h-rook is unmoved. */
    public boolean kingCastlePiecesReady(Piece.Color color);

    /** Return whether the king is unmoved and the a-rook is unmoved. */
    public boolean queenCastlePiecesReady(Piece.Color color);

    /**
     * Get the ChessPosition that results from a move.
     * @param move A Move object designating which move should be made.
     * @return A ChessPosition that is the result of making the move on
     *     this ChessPosition.
     */
    public ChessPosition moveResult(ChessMove move);

}
