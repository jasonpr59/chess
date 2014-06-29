package com.stalepretzel.chess.piece;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.stalepretzel.chess.CastlingMove;
import com.stalepretzel.chess.ChessMove;
import com.stalepretzel.chess.ChessPosition;
import com.stalepretzel.chess.NormalChessMove;
import com.stalepretzel.chess.PromotionMove;
import com.stalepretzel.chess.Square;

/**
 * A chess piece.
 * This class is immutable.
 */
public abstract class Piece {
    /** The color of this piece: white or black. */
    public enum Color{
        WHITE,
        BLACK;

        public Color opposite() {
            switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                throw new RuntimeException("Unexpected Color " + this);
            }
        }
    }

    private final Color color;

    /** Construct a new Piece with some type and color. */
    public Piece(Color color){
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Piece that = (Piece) obj;
        return color == that.getColor();
    }

    @Override
    public int hashCode() {
        return color.hashCode();
    }

    /**
     * Return an Iterable of sane ChessMoves for this Piece.
     * Requires that `position.getPiece(square).equals(this)`.
     */
    public abstract Iterable<ChessMove> saneMoves(Square start, ChessPosition position);

    /**
     * Return whether a NormalChessMove is sane for this piece.
     * @param move The ChessMove whose color-sanity is to be checked.
     * @param position The ChessPosition on which the move occurs.
     * Requires that `position.getPiece(move.getStart()).equals(this)`, i.e.
     * that the moving piece equal to this piece.
     */
    public abstract boolean isSane(NormalChessMove move, ChessPosition position);

    /**
     * Return whether a PromotionMove is sane for this piece.
     * @param move The PromotionMove whose color-sanity is to be checked.
     * @param position The ChessPosition on which the move occurs.
     * Requires that `position.getPiece(move.getStart()).equals(this)`, i.e.
     * that the moving piece equal to this piece.
     */
    public boolean isSane(PromotionMove move, ChessPosition position) {
        return false;
    }

    /**
     * Return whether a CastlingMove is sane for this piece.
     * @param move The CastlingMove whose color-sanity is to be checked.
     * @param position The ChessPosition on which the move occurs.
     * Requires that `position.getPiece(move.getStart()).equals(this)`, i.e.
     * that the moving piece equal to this piece.
     */
    public boolean isSane(CastlingMove move, ChessPosition position) {
        return false;
    }

    /**
     * Return whether a ChessMove is color-sane for this piece.
     * A move is color-sane if it satisfies the basic color-related
     * checks of a sanity check:
     *   A Piece can only move if it is of the to-move color.
     *   A captured Piece must be the opposite color of the capturing Piece.
     * @param move The ChessMove whose color-sanity is to be checked.
     * @param position The ChessPosition on which the move occurs.
     * Requires that `position.getPiece(move.getStart()).equals(this)`, i.e.
     * that the moving piece equal to this Piece.
     */
    public boolean isColorSane(ChessMove move, ChessPosition position) {
        Piece movingPiece = position.getPiece(move.getStart());
        Piece capturedPiece = position.getPiece(move.getEnd());

        if (movingPiece.getColor() != position.getToMoveColor()) {
            // This piece is not of the to-move color.
            return false;
        }
        if (capturedPiece != null && capturedPiece.getColor() == movingPiece.getColor()) {
            // Captures a piece of its own color!
            return false;
        }
        return true;
    }

    /** Return this Piece's sane moves from a set of moves. */
    public Collection<ChessMove> filterSane(Collection<ChessMove> candidates,
                                                   ChessPosition position) {
        Set<ChessMove> saneMoves = new HashSet<ChessMove>();
        for (ChessMove c : candidates) {
            if (c.isSane(position)) {
                saneMoves.add(c);
            }
        }
        return saneMoves;
    }
}
