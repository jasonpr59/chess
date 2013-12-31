package chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessMove;
import chess.ChessPosition;
import chess.Delta;
import chess.NormalChessMove;
import chess.Square;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public Iterable<ChessMove> saneMoves(Square start, ChessPosition position) {
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();

        // Knight moves are specified by three binary parameters:
        // Direction along rank, direction along file, and
        // alignment of the L-shape's long leg.
        int[] rankDirs = {-1, 1};
        int[] fileDirs = {-1, 1};
        int[][] alignments = {{1, 2}, {2, 1}};

        int dRank;
        int dFile;
        Delta delta;
        for (int rankDir : rankDirs) {
            for (int fileDir : fileDirs) {
                for (int[] alignment : alignments) {
                    dRank = rankDir * alignment[0];
                    dFile = fileDir * alignment[1];
                    delta = new Delta(dFile, dRank);
                    try {
                        candidateMoves.add(new NormalChessMove(start, delta));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // Swallow it. (See explanation in PAWN case).
                    }
                }
            }
        }
        return filterSane(candidateMoves, position);
    }

    @Override
    public boolean isSane(ChessMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }
        if (move instanceof NormalChessMove) {
            Delta moveDelta = move.getDelta();
            return Math.abs(moveDelta.getDeltaRank() * moveDelta.getDeltaFile()) == 2;
        } else {
            // Knights only make NormalChessMoves.
            return false;
        }
    }
}
