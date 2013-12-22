package chess;

public class CastlingInfo {
    // TODO: Cache all 2^6 possible CastlingInfos,
    // just pass around references to immutable instances.
    // (The fact that this is mutable is unsettling, since "immutable"
    // Boards keep a reference to a CastlingInfo.)
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;

    
    // These values are guaranteed to be correct whenever
    // the same-colored king has not moved.
    // (If the same-colored king *has* moved, then castling will
    // be illegal no matter whether the rook has moved, so we no
    // longer care whether the rooks have moved, and their values
    // are unspecified.)
    private boolean whiteKingRookMoved = false;
    private boolean blackKingRookMoved = false;
    
    private boolean whiteQueenRookMoved = false;
    private boolean blackQueenRookMoved = false;
    
    
    // Convenience squares, for deciding whether kings/rooks have moved.
    private static final Square E1 = Square.squareAt(5, 1);
    private static final Square E8 = Square.squareAt(5, 8);
    
    private static final Square H1 = Square.squareAt(8, 1);
    private static final Square H8 = Square.squareAt(8, 8);
    
    private static final Square A1 = Square.squareAt(1, 1);
    private static final Square A8 = Square.squareAt(1, 8);
    
    
    /**
     * Construct a CastlingInfo with no moved kings or rooks.
     */
    public CastlingInfo() {
    }

    /**
     * Construct a CastlingInfo identical to some source CastlingInfo.
     * @param that The source CastlingInfo.
     */
    public CastlingInfo(CastlingInfo that) {
        this.whiteKingMoved = that.isWhiteKingMoved();
        this.blackKingMoved = that.isBlackKingMoved();
        
        this.whiteKingRookMoved = that.isWhiteKingRookMoved();
        this.blackKingRookMoved = that.isBlackKingRookMoved();
        
        this.whiteQueenRookMoved = that.isWhiteQueenRookMoved();
        this.blackQueenRookMoved = that.isBlackQueenRookMoved();
    }

    public boolean isWhiteKingMoved() {
        return whiteKingMoved;
    }

    public boolean isBlackKingMoved() {
        return blackKingMoved;
    }

    public boolean isWhiteKingRookMoved() {
        return whiteKingRookMoved;
    }

    public boolean isBlackKingRookMoved() {
        return blackKingRookMoved;
    }

    public boolean isWhiteQueenRookMoved() {
        return whiteQueenRookMoved;
    }

    public boolean isBlackQueenRookMoved() {
        return blackQueenRookMoved;
    }

    /**
     * MODIFY this CastlingInfo to reflect the king/rook movements induced by a move.
     * Requires that the move is legal for the board that this CastlingInfo pertains to. 
     * @param move The move to account for.
     */
    public void update(Move move) {
        // If any move starts or ends at a square of interest, then the
        // piece whose home was that square has either moved (in this move
        // or a previous move), or been captured.  In any such case, it means
        // the piece whose home was that square has moved in some way or another.
        whiteKingMoved |= move.startsOrEndsAt(E1);
        blackKingMoved |= move.startsOrEndsAt(E8);
        
        whiteKingRookMoved |= move.startsOrEndsAt(H1);
        blackKingRookMoved |= move.startsOrEndsAt(H8);
    
        whiteQueenRookMoved |= move.startsOrEndsAt(A1);
        blackQueenRookMoved |= move.startsOrEndsAt(A8);
    }
    
    /**
     * @return true iff the king is unmoved and the h-rook is unmoved.
     */
    public boolean kingCastlePiecesReady(Piece.Color color) {
        if (color == Piece.Color.WHITE){
            return !whiteKingMoved && !whiteKingRookMoved;
        } else {
            return !blackKingMoved && !blackKingRookMoved;
        }
    }
    
    /**
     * @return true iff the king is unmoved and the a-rook is unmoved.
     */
    public boolean queenCastlePiecesReady(Piece.Color color) {
        if (color == Piece.Color.WHITE){
            return !whiteKingMoved && !whiteQueenRookMoved;
        } else {
            return !blackKingMoved && !blackQueenRookMoved;
        }
    }
    
}
