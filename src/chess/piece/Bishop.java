package chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessMove;
import chess.ChessPosition;
import chess.Delta;
import chess.NormalChessMove;
import chess.Square;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public Iterable<ChessMove> saneMoves(Square start, ChessPosition position) {
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();
        Collection<Square> candidateEnds = start.explore(Delta.DIAGONAL_DIRS);
        candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        return filterSane(candidateMoves, position);
    }

    @Override
    public boolean isSane(ChessMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }
        if (move instanceof NormalChessMove) {
            return move.getDelta().isDiagonal() && move.isOpen(position);
        } else {
            // Bishops only make NormalChessMoves.
            return false;
        }
    }
}
