package main.java.chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import main.java.chess.ChessMove;
import main.java.chess.ChessPosition;
import main.java.chess.Delta;
import main.java.chess.NormalChessMove;
import main.java.chess.Square;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }

    @Override
    public Iterable<ChessMove> saneMoves(Square start, ChessPosition position) {
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();
        Iterable<Square> candidateEnds = start.explore(Delta.QUEEN_DIRS);
        candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        return filterSane(candidateMoves, position);
    }

    @Override
    public boolean isSane(NormalChessMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }
        Delta delta = new Delta(move);
        return ((delta.isBasic() || delta.isDiagonal()) &&
                !position.anyOccupied(move.passedThrough()));
    }
}
