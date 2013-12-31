package chess.piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import chess.CastlingMove;
import chess.ChessMove;
import chess.ChessMoveUtil;
import chess.ChessPosition;
import chess.Delta;
import chess.Square;

public class King extends Piece {
    public King(Color color) {
        super(color);
    }

    @Override
    public Iterable<ChessMove> saneMoves(Square start, ChessPosition position) {
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();
        Collection<Square> candidateEnds = start.explore(Delta.QUEEN_DIRS, 1);
        candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        if (start.getFile() == 5) {
            // There's a decent chance that the king's in its home square,
            // and a zero chance that a two-square hop along a rank will
            // put us off the board.
            // TODO: Figure out how to add these directly to candidateMoves,
            // without making the generics gods sad.
            Collection<CastlingMove> castlingMoves = new HashSet<CastlingMove>();
            castlingMoves.add(new CastlingMove(start, new Delta(2, 0)));
            castlingMoves.add(new CastlingMove(start, new Delta(-2, 0)));
            candidateMoves.addAll(castlingMoves);
        }
        return ChessMoveUtil.filterSane(candidateMoves, position);
    }
}
