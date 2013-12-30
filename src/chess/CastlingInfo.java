package chess;

import chess.piece.Piece;

/**
 * Stores whether certain pieces have moved, for deciding whether castling is legal.
 * This class is immutable.
 */
public class CastlingInfo {

    public enum Side { KINGSIDE, QUEENSIDE; }

    // There are only 2^4 = 16 possible CastlingInfos, since
    // a CastlingInfo is defined by 4 booleans.  We generate
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
        ALL = new CastlingInfo[16];
        for (byte id = 0; id < 16; id++) {
            ALL[id] = new CastlingInfo(id);
        }
    }

    @Override
    public int hashCode() {
        return id;
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
        return id == other.id;
    }

    // The four booleans that completely define the CastlingInfo.
    private final boolean whiteCanKingCastle;
    private final boolean whiteCanQueenCastle;
    private final boolean blackCanKingCastle;
    private final boolean blackCanQueenCastle;

    // We cache the id
    private final byte id;

    // Flags used for computing ids from "moved values."
    // WCKC = "white can king castle", etc.
    // One might argue that these abbreviations are unacceptable,
    // but if you're using these flags anywhere except *right* next
    // to the unabbreviated boolean value names, you're doing something
    // wrong.
    private static final int WCKC_FLAG = 1;
    private static final int WCQC_FLAG = 2;
    private static final int BCKC_FLAG = 4;
    private static final int BCQC_FLAG = 8;

    /** Create the CastlingInfo with the specified id. */
    private CastlingInfo(byte id) {
        if (id < 0 || id >= 64) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        whiteCanKingCastle = (id & WCKC_FLAG) != 0;
        whiteCanQueenCastle = (id & WCQC_FLAG) != 0;
        blackCanKingCastle = (id & BCKC_FLAG) != 0;
        blackCanQueenCastle = (id & BCQC_FLAG) != 0;
    }

    /** Return a CastlingInfo with the specified "moved values." */
    private static CastlingInfo fromValues(final boolean whiteCanKingCastle,
                                           final boolean whiteCanQueenCastle,
                                           final boolean blackCanKingCastle,
                                           final boolean blackCanQueenCastle) {
        byte id = id(whiteCanKingCastle, whiteCanQueenCastle,
                     blackCanKingCastle, blackCanQueenCastle);
        return fromId(id);
    }

    /** Return the id of the CastlingInfo with the specified "moved values." */
    private static byte id(final boolean whiteCanKingCastle,
                           final boolean whiteCanQueenCastle,
                           final boolean blackCanKingCastle,
                           final boolean blackCanQueenCastle) {
        return (byte) ((whiteCanKingCastle ? WCKC_FLAG : 0) +
                       (whiteCanQueenCastle ? WCQC_FLAG : 0) +
                       (blackCanKingCastle ? BCKC_FLAG : 0) +
                       (blackCanQueenCastle ? BCQC_FLAG : 0));
    }

    /** Return the CastlingInfo with the specified id. */
    private static CastlingInfo fromId(byte id) {
        return ALL[id];
    }

    /**
     * Return a CastlingInfo with no moved kings or rooks.
     */
    public static CastlingInfo allowAll() {
        return fromValues(true, true, true, true);
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

        boolean whiteCanKingCastle = (this.whiteCanKingCastle &&
                                      !move.startsOrEndsAt(E1) &&
                                      !move.startsOrEndsAt(H1));

        boolean whiteCanQueenCastle = (this.whiteCanQueenCastle &&
                                       !move.startsOrEndsAt(E1) &&
                                       !move.startsOrEndsAt(A1));

        boolean blackCanKingCastle = (this.blackCanKingCastle &&
                                      !move.startsOrEndsAt(E8) &&
                                      !move.startsOrEndsAt(H8));

        boolean blackCanQueenCastle = (this.blackCanQueenCastle &&
                                       !move.startsOrEndsAt(E8) &&
                                       !move.startsOrEndsAt(A8));

        return fromValues(whiteCanKingCastle, whiteCanQueenCastle,
                          blackCanKingCastle, blackCanQueenCastle);
    }

    /** Return whether the king is unmoved AND the h-rook is unmoved. */
    public boolean castlePiecesReady(Piece.Color color, Side side) {
        if (side == Side.KINGSIDE){
            return (color == Piece.Color.WHITE) ? whiteCanKingCastle : blackCanKingCastle;
        } else {
            return (color == Piece.Color.WHITE) ? whiteCanQueenCastle : blackCanQueenCastle;
        }
    }
}
