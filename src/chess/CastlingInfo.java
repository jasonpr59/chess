package chess;

/**
 * Stores whether certain pieces have moved, for deciding whether castling is legal.
 * This class is immutable.
 */
public class CastlingInfo {

    public enum Side { KINGSIDE, QUEENSIDE; }

    // There are only 2^6 = 64 possible CastlingInfos, since
    // a CastlingInfo is defined by 6 booleans.  We generate
    // all of them statically, then pass around references to
    // these (immutable) CastlingInfos.
    private static CastlingInfo[] ALL;

    // Convenience squares, for deciding whether kings/rooks have moved.
    private static final Square E1 = Square.squareAt(5, 1);
    private static final Square E8 = Square.squareAt(5, 8);

    private static final Square H1 = Square.squareAt(8, 1);
    private static final Square H8 = Square.squareAt(8, 8);

    private static final Square A1 = Square.squareAt(1, 1);
    private static final Square A8 = Square.squareAt(1, 8);

    static {
        // Generate all the possible CastlingInfos, ahead of time.
        // Each one
        ALL = new CastlingInfo[64];
        for (byte id = 0; id < 64; id++) {
            ALL[id] = new CastlingInfo(id);
        }
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CastlingInfo other = (CastlingInfo) obj;
        return id == other.getId();
    }

    // The six booleans that completely define the CastlingInfo.
    private final boolean whiteKingMoved;
    private final boolean blackKingMoved;
    private final boolean whiteKingRookMoved;
    private final boolean blackKingRookMoved;
    private final boolean whiteQueenRookMoved;
    private final boolean blackQueenRookMoved;

    // We cache the id
    private final byte id;

    // Flags used for computing ids from "moved values."
    // WKM = "white king moved", etc.
    // One might argue that these abbreviations are unacceptable,
    // but if you're using these flags anywhere except *right* next
    // to the unabbreviated boolean value names, you're doing something
    // wrong.
    private static final int WKM_FLAG = 1;
    private static final int BKM_FLAG = 2;
    private static final int WKRM_FLAG = 4;
    private static final int BKRM_FLAG = 8;
    private static final int WQRM_FLAG = 16;
    private static final int BQRM_FLAG = 32;

    /** Create the CastlingInfo with the specified id. */
    private CastlingInfo(byte id) {
        if (id < 0 || id >= 64) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        whiteKingMoved = (id & WKM_FLAG) != 0;
        blackKingMoved = (id & BKM_FLAG) != 0;
        whiteKingRookMoved = (id & WKRM_FLAG) != 0;
        blackKingRookMoved = (id & BKRM_FLAG) != 0;
        whiteQueenRookMoved = (id & WQRM_FLAG) != 0;
        blackQueenRookMoved = (id & BQRM_FLAG) != 0;
    }

    /** Return a CastlingInfo with the specified "moved values." */
    private static CastlingInfo fromValues(final boolean whiteKingMoved,
                                           final boolean blackKingMoved,
                                           final boolean whiteKingRookMoved,
                                           final boolean blackKingRookMoved,
                                           final boolean whiteQueenRookMoved,
                                           final boolean blackQueenRookMoved) {
        byte id = id(whiteKingMoved, blackKingMoved,
                     whiteKingRookMoved, blackKingRookMoved,
                     whiteQueenRookMoved, blackQueenRookMoved);
        return fromId(id);
    }

    /** Return the id of the CastlingInfo with the specified "moved values." */
    private static byte id(final boolean whiteKingMoved,
                           final boolean blackKingMoved,
                           final boolean whiteKingRookMoved,
                           final boolean blackKingRookMoved,
                           final boolean whiteQueenRookMoved,
                           final boolean blackQueenRookMoved) {
        return (byte) ((whiteKingMoved ? WKM_FLAG : 0) +
                       (blackKingMoved ? BKM_FLAG : 0) +
                       (whiteKingRookMoved ? WKRM_FLAG : 0) +
                       (blackKingRookMoved ? BKRM_FLAG : 0) +
                       (whiteQueenRookMoved ? WQRM_FLAG : 0) +
                       (blackQueenRookMoved ? BQRM_FLAG : 0));
    }

    /** Return the CastlingInfo with the specified id. */
    private static CastlingInfo fromId(byte id) {
        return ALL[id];
    }

    /**
     * Return a CastlingInfo with no moved kings or rooks.
     */
    public static CastlingInfo allowAll() {
        return fromValues(false, false, false, false, false, false);
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

    public byte getId() {
        return id;
    }

    /**
     * Get a copy of this CastlingInfo that reflects the king/rook movements induced by a move.
     * Requires that the move is legal for the board that this CastlingInfo pertains to.
     * @param move The move to account for.
     */
    public CastlingInfo updated(ChessMove move) {
        // If any move starts or ends at a square of interest, then the
        // piece whose home was that square has either moved (in this move
        // or a previous move), or been captured.  In any such case, it means
        // the piece whose home was that square has moved in some way or another.
        boolean whiteKingMoved = this.whiteKingMoved || move.startsOrEndsAt(E1);
        boolean blackKingMoved = this.blackKingMoved || move.startsOrEndsAt(E8);

        boolean whiteKingRookMoved = this.whiteKingRookMoved || move.startsOrEndsAt(H1);
        boolean blackKingRookMoved = this.blackKingRookMoved || move.startsOrEndsAt(H8);

        boolean whiteQueenRookMoved = this.whiteQueenRookMoved || move.startsOrEndsAt(A1);
        boolean blackQueenRookMoved = this.blackQueenRookMoved || move.startsOrEndsAt(A8);

        return fromValues(whiteKingMoved, blackKingMoved, whiteKingRookMoved,
                          blackKingRookMoved, whiteQueenRookMoved, blackQueenRookMoved);
    }

    /** Return whether the king is unmoved AND the h-rook is unmoved. */
    public boolean castlePiecesReady(Piece.Color color, Side side) {
        if (side == Side.KINGSIDE){
            if (color == Piece.Color.WHITE){
                return !whiteKingMoved && !whiteKingRookMoved;
            } else {
                return !blackKingMoved && !blackKingRookMoved;
            }
        } else {
            if (color == Piece.Color.WHITE){
                return !whiteKingMoved && !whiteQueenRookMoved;
            } else {
                return !blackKingMoved && !blackQueenRookMoved;
            }
        }
    }
}
