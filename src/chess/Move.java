package chess;

public class Move {

    private final Square start;
    private final Square end;
    private final int deltaFile;
    private final int deltaRank;

    public Move(Square start, Square end) {
        this.start = start;
        this.end = end;

        deltaFile = end.getFile() - start.getFile();
        deltaRank = end.getRank() - start.getRank();
        
        if (deltaFile == 0 && deltaRank == 0) {
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
        
        switch (board.getPiece(start).getType()) {
            case PAWN:
                // TODO(jasonpr): Implement.
                break;
            case KNIGHT:
                return Math.abs(deltaRank * deltaFile) == 2; 
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
                return (Math.abs(deltaRank) <= 1 && Math.abs(deltaFile) <= 1);
                break;
        }
    }
    
    private boolean isDiagonal() {
        return Math.abs(deltaRank) == Math.abs(deltaFile);
    }
        
    private boolean isBasic() {
        return (deltaRank == 0) ^ (deltaFile == 0);
    }
    
    /**
     * Return whether the move is a Knight-style L-shaped move.
     */
    private boolean isLShaped() {
        // Math!
            }
    
}
