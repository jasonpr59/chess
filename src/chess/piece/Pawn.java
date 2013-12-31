package chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessMove;
import chess.ChessMoveUtil;
import chess.ChessPosition;
import chess.Delta;
import chess.NormalChessMove;
import chess.PromotionMove;
import chess.Square;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    public Iterable<ChessMove> saneMoves(Square start, ChessPosition position) {
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();

        boolean isWhite = getColor() == Piece.Color.WHITE;
        Delta fwd = new Delta(0, isWhite? 1 : -1);
        Collection<Delta> candidateDeltas = new ArrayList<Delta>();
        // There are at most four possible pawn moves (ignoring promotion choices).
        // Just try them all!
        candidateDeltas.add(fwd);
        candidateDeltas.add(fwd.scaled(2));
        candidateDeltas.add(Delta.sum(fwd, new Delta(1, 0)));
        candidateDeltas.add(Delta.sum(fwd,  new Delta(-1, 0)));

        NormalChessMove candidateMove;
        for (Delta delta : candidateDeltas) {
            try {
                candidateMove = new NormalChessMove(start, delta);
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
            int endRank = candidateMove.getEnd().getRank();
            if (endRank == 8 || endRank == 1) {
                // It's a promotion!
                candidateMoves.addAll(PromotionMove.allPromotions(candidateMove));
            } else {
                // It's a non-promotion.
                candidateMoves.add(candidateMove);
            }
        }
        return ChessMoveUtil.filterSane(candidateMoves, position);
    }

}
