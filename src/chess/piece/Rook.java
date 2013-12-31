package chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessMove;
import chess.ChessMoveUtil;
import chess.ChessPosition;
import chess.Delta;
import chess.Square;

public class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }

    @Override
    public Iterable<ChessMove> saneMoves(Square start, ChessPosition position) {
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();
        Collection<Square> candidateEnds = start.explore(Delta.BASIC_DIRS);
        candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        return ChessMoveUtil.filterSane(candidateMoves, position);
    }
}
