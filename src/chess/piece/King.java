package chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import chess.CastlingInfo;
import chess.CastlingMove;
import chess.ChessMove;
import chess.ChessPosition;
import chess.Delta;
import chess.NormalChessMove;
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
            candidateMoves.add(new CastlingMove(start, new Delta(2, 0)));
            candidateMoves.add(new CastlingMove(start, new Delta(-2, 0)));
        }
        return filterSane(candidateMoves, position);
    }

    @Override
    public boolean isSane(ChessMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }

        if (move instanceof NormalChessMove) {
            Delta delta = move.getDelta();
            return (Math.abs(delta.getDeltaRank()) <= 1 && Math.abs(delta.getDeltaFile()) <= 1);
        } else if (move instanceof CastlingMove) {
            Square start = move.getStart();

            // Ensure the start square is a king square.
            if (!start.equals(Square.algebraic("e1")) &&
                    !start.equals(Square.algebraic("e8"))) {
                return false;
            }

            // Ensure the Delta is a castling delta.
            Delta delta = move.getDelta();
            CastlingInfo.Side side;
            if (delta.equals(Delta.KING_CASTLE_DELTA)) {
                side = CastlingInfo.Side.KINGSIDE;
            } else if (delta.equals(Delta.QUEEN_CASTLE_DELTA)) {
                side = CastlingInfo.Side.QUEENSIDE;
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

            // Finally, just ensure that there's space all the way between the king
            // and its rook.
            Square rookStart = CastlingMove.getRookStart(getColor(), side);
            // There's space if the ChessMove from the king square to the rook square
            // is open.
            return new NormalChessMove(start, rookStart).isOpen(position);
        } else {
            // Kings only make NormalChessMoves and CastlingMoves.
            return false;
        }

    }
}
