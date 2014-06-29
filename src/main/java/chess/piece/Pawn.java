package main.java.chess.piece;

import java.util.ArrayList;
import java.util.Collection;

import main.java.chess.ChessMove;
import main.java.chess.ChessPosition;
import main.java.chess.Delta;
import main.java.chess.NormalChessMove;
import main.java.chess.PromotionMove;
import main.java.chess.Square;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
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

    /**
     * Return whether a NormalChessMove is sane, except possibly for pushing too far.
     *
     * Normally, it's illegal for a pawn to push to its last rank without promoting.
     * But, it can be useful to ask, "would this NormalChessMove be sane, if not
     * for the fact the pawn doesn't promote?"  This method allows us to ask that question.
     * @param pawnCanPushToEdge Whether to allow a pawn to push "sanely" to its
     *      promotion rank, even though the NormalChessMove is not a PromotionMove.
     */
    private boolean isSane(NormalChessMove move, ChessPosition position, boolean pawnCanPushToEdge) {
        if (!isColorSane(move, position)) {
            return false;
        }

        Delta moveDelta = new Delta(move);
        Square start = move.getStart();
        Square end = move.getEnd();

        if (!pawnCanPushToEdge && end.getRank() == getPromotionRank()) {
            return false;
        }

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
    public boolean isSane(NormalChessMove move, ChessPosition position) {
        return isSane(move, position, false);
    }

    @Override
    public boolean isSane(PromotionMove move, ChessPosition position) {
        if (!isColorSane(move, position)) {
            return false;
        }
        if (move.getEnd().getRank() != getPromotionRank()) {
            return false;
        }

        PromotionMove promotionMove = move;
        return isSane(promotionMove.getBaseMove(), position, true);
    }

    private int getPromotionRank() {
        return getColor() == Piece.Color.WHITE ? 8 : 1;
    }
}
