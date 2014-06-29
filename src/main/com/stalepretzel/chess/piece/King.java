package com.stalepretzel.chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import com.stalepretzel.chess.CastlingInfo;
import com.stalepretzel.chess.CastlingMove;
import com.stalepretzel.chess.ChessMove;
import com.stalepretzel.chess.ChessPosition;
import com.stalepretzel.chess.Delta;
import com.stalepretzel.chess.NormalChessMove;
import com.stalepretzel.chess.Square;

public class King extends Piece {
    public King(Color color) {
        super(color);
    }

    @Override
    public Iterable<ChessMove> saneMoves(Square start, ChessPosition position) {
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();
        Iterable<Square> candidateEnds = start.explore(Delta.QUEEN_DIRS, 1);
        candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        if (start.getFile() == 5) {
            // There's a decent chance that the king's in its home square,
            // and a zero chance that a two-square hop along a rank will
            // put us off the board.
            candidateMoves.add(new CastlingMove(CastlingMove.Side.KINGSIDE,
                                                position.getToMoveColor()));
            candidateMoves.add(new CastlingMove(CastlingMove.Side.QUEENSIDE,
                                                position.getToMoveColor()));
        }
        return filterSane(candidateMoves, position);
    }

    @Override
    public boolean isSane(NormalChessMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }
        Delta delta = new Delta(move);
        return (Math.abs(delta.getDeltaRank()) <= 1 && Math.abs(delta.getDeltaFile()) <= 1);
    }

    @Override
    public boolean isSane(CastlingMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }
        Square start = move.getStart();

        // Ensure the start square is a king square.
        if (!start.equals(Square.algebraic("e1")) &&
                !start.equals(Square.algebraic("e8"))) {
            return false;
        }

        // Ensure the Delta is a castling delta.
        Delta delta = new Delta(move);
        CastlingMove.Side side;
        if (delta.equals(Delta.KING_CASTLE_DELTA)) {
            side = CastlingMove.Side.KINGSIDE;
        } else if (delta.equals(Delta.QUEEN_CASTLE_DELTA)) {
            side = CastlingMove.Side.QUEENSIDE;
        } else {
            return false;
        }

        // Ensure the pieces are ready to castle.
        CastlingInfo castlingInfo = position.getCastlingInfo();
        // If the moving piece is a king in its OWN home square, then
        // this CastlingInfo check will ensure that he is ready to castle.
        // If the moving piece is a king in the OTHER king's home square, then
        // neither king is in his own home square, so neither king is ready to
        // castle, and this check will return false, correctly.
        if (!castlingInfo.castlePiecesReady(getColor(), side)) {
            return false;
        }

        return !position.anyOccupied(move.passedThrough());
    }
}
