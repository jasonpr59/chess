package com.stalepretzel.chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import com.stalepretzel.chess.ChessMove;
import com.stalepretzel.chess.ChessPosition;
import com.stalepretzel.chess.Delta;
import com.stalepretzel.chess.NormalChessMove;
import com.stalepretzel.chess.Square;

public class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }

    @Override
    public Iterable<ChessMove> saneMoves(Square start, ChessPosition position) {
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();
        Iterable<Square> candidateEnds = start.explore(Delta.BASIC_DIRS);
        candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        return filterSane(candidateMoves, position);
    }

    @Override
    public boolean isSane(NormalChessMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }
        return new Delta(move).isBasic() && !position.anyOccupied(move.passedThrough());
    }
}
