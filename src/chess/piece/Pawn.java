package chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessMove;
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
        return filterSane(candidateMoves, position);
    }

    @Override
    public boolean isSane(NormalChessMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }

        Delta moveDelta = move.getDelta();
        Square start = move.getStart();
        Square end = move.getEnd();

        // We'll need to know which way is forward for this pawn, later on.
        boolean isWhite = getColor() == Piece.Color.WHITE;
        Delta forward = new Delta(0, isWhite ? 1: -1);
        Delta doubleForward = forward.scaled(2);

        if (moveDelta.getDeltaFile() == 0) {
            // Pawn push.
            // Square one ahead must be unoccupied, whether single- or
            // double-push.
            // Note, isLandable is NOT enough... it must be UNOCCUPIED, not just landable.
            if (position.getPiece(start.plus(forward)) != null) {
                return false;
            }

            if (moveDelta.equals(forward)) {
                return true;
            } else if (moveDelta.equals(doubleForward)) {
                return (start.isOnPawnHomeRank(getColor()) &&
                        position.getPiece(start.plus(doubleForward)) == null);
            } else {
                return false;
            }
        } else if (Math.abs(moveDelta.getDeltaFile()) == 1) {
            // Capture.
            if (moveDelta.getDeltaRank() != forward.getDeltaRank()) {
                return false;
            }
            Piece capturedPiece = position.getPiece(end);
            boolean normalCapture = (capturedPiece != null &&
                    capturedPiece.getColor() != getColor());
            boolean epCapture = end.equals(position.getEnPassantSquare());
            return normalCapture || epCapture;
        } else {
            return false;
        }
    }

    @Override
    public boolean isSane(PromotionMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }
        int promotableRank;
        if (getColor() == Piece.Color.WHITE) {
            promotableRank = 8;
        } else {
            promotableRank = 1;
        }
        if (move.getEnd().getRank() != promotableRank) {
            return false;
        }

        PromotionMove promotionMove = (PromotionMove) move;
        return isSane(promotionMove.getBaseMove(), position);
    }
}
