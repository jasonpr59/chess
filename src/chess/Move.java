package chess;

public class Move {

    private final Square start;
    private final Square end;
    private final Delta delta;

    public Move(Square start, Square end) {
        this.start = start;
        this.end = end;

        // Acceptable to use "this" here: Delta only uses start and end,
        // which were already set.
        delta = new Delta(this);
        
        if (delta.getDeltaFile() == 0 && delta.getDeltaRank() == 0) {
            throw new IllegalArgumentException("A move cannot start and end on the same square.");
        }
    }
    
    public Square getStart() {
        return start;
    }

    public Square getEnd() {
        return end;
    }

    /**
     * Return whether this move is sane on a particular board.
     * 
     * A move is sane if it is legal, IGNORING the no-check criterion.
     * That is, assert that the move is legal, except that the king might
     * be checked, or might have castled through or out of check.
     */
    public boolean isSane(Board board) {
        if (!isLandable(board)) {
            return false;
        }
        
        Piece movingPiece = board.getPiece(start);
        
        switch (movingPiece.getType()) {
            case PAWN:
                // We'll need to know which way is forward for this pawn, later on.
                boolean isWhite = movingPiece.getPieceColor() == Piece.PieceColor.WHITE;
                Delta forward = new Delta(0, isWhite ? 1: -1);
                Delta doubleForward = forward.scaled(2);

                if (delta.getDeltaFile() == 0) {
                    // Pawn push.
                    // Square one ahead must be unoccupied, whether single- or
                    // double-push.
                    // Note, isLandable is NOT enough... it must be UNOCCUPIED, not just landable.
                    if (board.getPiece(start.plus(forward)) != null) {
                        return false;
                    }

                    if (delta.equals(forward)) {
                        return true;
                    } else if (delta.equals(doubleForward)) {
                        return board.getPiece(start.plus(doubleForward)) == null;
                    } else {
                        return false;
                    }
                } else if (Math.abs(delta.getDeltaFile()) == 1) {
                    // Capture.
                    if (delta.getDeltaRank() != forward.getDeltaRank()) {
                        return false;
                    }
                    Piece capturedPiece = board.getPiece(end);
                    boolean normalCapture = (capturedPiece != null &&
                                             capturedPiece.getPieceColor() != movingPiece.getPieceColor());
                    boolean epCapture = end.equals(board.getEnPassantSquare());
                    return normalCapture || epCapture;
                } else {
                    return false;
                }
                break;
            case KNIGHT:
                return Math.abs(delta.getDeltaRank() * delta.getDeltaFile()) == 2; 
                break;
            case BISHOP:
                return isDiagonal() && isOpen(board);
                break;
            case ROOK:
                return isBasic() && isOpen(board);
                break;
            case QUEEN:
                return (isBasic() || isDiagonal()) && isOpen(board);
                break;
            case KING:
                // TODO(jasonpr): Handle castling!
                return (Math.abs(delta.getDeltaRank()) <= 1 && Math.abs(delta.getDeltaFile()) <= 1);
                break;
        }
    }
    
    private boolean isLandable(Board board) {
        Piece movingPiece = board.getPiece(start);
        Piece capturedPiece = board.getPiece(end);
        return (capturedPiece == null ||
                capturedPiece.getPieceColor() != movingPiece.getPieceColor());
    }

    private boolean isDiagonal() {
        return Math.abs(delta.getDeltaRank()) == Math.abs(delta.getDeltaFile());
    }
        
    private boolean isBasic() {
        return (delta.getDeltaRank() == 0) ^ (delta.getDeltaFile() == 0);
    }
}
