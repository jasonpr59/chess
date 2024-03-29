package com.stalepretzel.chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.stalepretzel.chess.piece.Bishop;
import com.stalepretzel.chess.piece.Knight;
import com.stalepretzel.chess.piece.Piece;
import com.stalepretzel.chess.piece.Queen;
import com.stalepretzel.chess.piece.Rook;

/** A pawn-promotion chess move. */
public class PromotionMove implements ChessMove {
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + baseMove.hashCode();
        result = prime * result + promotedPiece.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PromotionMove other = (PromotionMove) obj;
        if (!baseMove.equals(other.baseMove)) {
            return false;
        }
        if (!promotedPiece.equals(other.promotedPiece)) {
            return false;
        }
        return true;
    }

    private final Piece promotedPiece;
    private final NormalChessMove baseMove;

    /** Create a PromotionMove from a normal move and a promoted Piece. */
    public PromotionMove(NormalChessMove move, Piece promotedPiece) {
        this.baseMove = move;
        this.promotedPiece = promotedPiece;
    }

    public NormalChessMove getBaseMove() {
        return baseMove;
    }

    public Piece getPromotedPiece() {
        return promotedPiece;
    }

    /**
     * Return all PromotionMoves that a Move could beget.
     * That is, create four PromotionMoves from one ChessMove,
     * one for each promotion type (knight, bishop, rook, queen).
     */
    public static Collection<PromotionMove> allPromotions(NormalChessMove move) {
        List<PromotionMove> allPromotions = new ArrayList<PromotionMove>();
        Piece.Color color = getPromotingColor(move);
        allPromotions.add(new PromotionMove(move, new Knight(color)));
        allPromotions.add(new PromotionMove(move, new Bishop(color)));
        allPromotions.add(new PromotionMove(move, new Rook(color)));
        allPromotions.add(new PromotionMove(move, new Queen(color)));
        return allPromotions;
    }

    @Override
    public boolean isSane(ChessPosition position) {
        Piece movingPiece = position.getPiece(baseMove.getStart());
        if (movingPiece == null) {
            return false;
        }
        return movingPiece.isSane(this, position);
    }

    @Override
    public ChessPosition result(ChessPosition position) {
        // Make the pawn move to the last rank, normally.
        ChessPosition partlyMoved = baseMove.result(position);

        // Convert it to its promoted type.
        // TODO: Do this without creating a second builder for the promotion
        // step.  (The first one was in the super.result step.)
        ChessPositionBuilder builder = new ChessPositionBuilder(partlyMoved);
        Square end = baseMove.getEnd();
        builder.placePiece(promotedPiece, end);

        return builder.build();
    }

    @Override
    public Square getStart() {
        return baseMove.getStart();
    }

    @Override
    public Square getEnd() {
        return baseMove.getEnd();
    }

    @Override
    public Square capturedSquare(ChessPosition position) {
        return baseMove.capturedSquare(position);
    }

    @Override
    public Square enPassantSquare(ChessPosition position) {
        return null;
    }

    @Override
    public boolean isLegal(ChessPosition position) {
        // All legal Moves are sane.
        if (!isSane(position)) {
            return false;
        }
        ChessPosition resultBoard = result(position);
        return !resultBoard.checked(position.getToMoveColor());
    }

    private static Piece.Color getPromotingColor(NormalChessMove move) {
        if (move.getEnd().getRank() == 8) {
            return Piece.Color.WHITE;
        } else {
            return Piece.Color.BLACK;
        }
    }

    @Override
    public Iterable<Square> passedThrough() {
        // TODO: Figure out if there's a better way
        // to return an empty Iterable.
        return new ArrayList<Square>();
    }
}
