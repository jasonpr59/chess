package test.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import chess.CastlingInfo;
import chess.ChessMove;
import chess.Piece;

public class CastlingInfoTest {

    // Convenience shortcuts, so we don't need to type out
    // fully qualified names a zillion times.
    private static final Piece.Color WHITE = Piece.Color.WHITE;
    private static final Piece.Color BLACK = Piece.Color.BLACK;
    private static final CastlingInfo.Side KINGSIDE = CastlingInfo.Side.KINGSIDE;
    private static final CastlingInfo.Side QUEENSIDE = CastlingInfo.Side.QUEENSIDE;

    @Test
    public void testAllowAll() {
        CastlingInfo allAllowed = CastlingInfo.allowAll();
        for (Piece.Color color : Piece.Color.values()) {
            for (CastlingInfo.Side side : CastlingInfo.Side.values()) {
                assertTrue(allAllowed.castlePiecesReady(color, side));
            }
        }
    }

    @Test
    public void testMovesFromRookSquare() {
        // We're going to test this into oblivion, since the implementation
        // is pretty error-prone.

        CastlingInfo base = CastlingInfo.allowAll();

        // Assert that moving the white king rook makes it
        // impossible for white to kingside castle.
        CastlingInfo whiteKingRookMoved = base.updated(new ChessMove("h1", "h2"));
        assertFalse(whiteKingRookMoved.castlePiecesReady(WHITE, KINGSIDE));
        assertTrue(whiteKingRookMoved.castlePiecesReady(WHITE, QUEENSIDE));
        for (CastlingInfo.Side side : CastlingInfo.Side.values()) {
            assertTrue(whiteKingRookMoved.castlePiecesReady(BLACK, side));
        }

        // Assert that moving the white queen rook makes it
        // impossible for white to queenside castle.
        CastlingInfo whiteQueenRookMoved = base.updated(new ChessMove("a1", "a2"));
        assertTrue(whiteQueenRookMoved.castlePiecesReady(WHITE, KINGSIDE));
        assertFalse(whiteQueenRookMoved.castlePiecesReady(WHITE, QUEENSIDE));
        for (CastlingInfo.Side side : CastlingInfo.Side.values()) {
            assertTrue(whiteQueenRookMoved.castlePiecesReady(BLACK, side));
        }

        // Assert that moving the black king rook makes it
        // impossible for black to kingside castle.
        CastlingInfo blackKingRookMoved = base.updated(new ChessMove("h8", "h7"));
        for (CastlingInfo.Side side : CastlingInfo.Side.values()) {
            assertTrue(blackKingRookMoved.castlePiecesReady(WHITE, side));
        }
        assertFalse(blackKingRookMoved.castlePiecesReady(BLACK, KINGSIDE));
        assertTrue(blackKingRookMoved.castlePiecesReady(BLACK, QUEENSIDE));

        // Assert that moving the black queen rook makes it
        // impossible for black to queenside castle.
        CastlingInfo blackQueenRookMoved = base.updated(new ChessMove("a8", "a7"));
        for (CastlingInfo.Side side : CastlingInfo.Side.values()) {
            assertTrue(blackQueenRookMoved.castlePiecesReady(WHITE, side));
        }
        assertTrue(blackQueenRookMoved.castlePiecesReady(BLACK, KINGSIDE));
        assertFalse(blackQueenRookMoved.castlePiecesReady(BLACK, QUEENSIDE));
    }

    @Test
    public void testMovesFromKingSquare() {
        CastlingInfo base = CastlingInfo.allowAll();

        // Assert that moving the white king makes it
        // impossible for white to castle.
        CastlingInfo whiteKingMoved = base.updated(new ChessMove("e1", "e2"));
        for (Piece.Color color : Piece.Color.values()) {
            for (CastlingInfo.Side side : CastlingInfo.Side.values()) {
                // Only black can castle, and black can castle on either side.
                assertEquals(color == Piece.Color.BLACK,
                             whiteKingMoved.castlePiecesReady(color, side));
            }
        }

        // Assert that moving the black king makes it
        // impossible for black to castle.
        CastlingInfo blackKingMoved = base.updated(new ChessMove("e8", "e7"));
        for (Piece.Color color : Piece.Color.values()) {
            for (CastlingInfo.Side side : CastlingInfo.Side.values()) {
                // Only white can castle, and white can castle on either side.
                assertEquals(color == Piece.Color.WHITE,
                             blackKingMoved.castlePiecesReady(color, side));
            }
        }
    }

    @Test
    public void testMoveWithNoEffect() {
        ChessMove noEffect = new ChessMove("e5", "e6");
        CastlingInfo stillUnrestricted = CastlingInfo.allowAll().updated(noEffect);
        for (Piece.Color color : Piece.Color.values()) {
            for (CastlingInfo.Side side : CastlingInfo.Side.values()) {
                assertTrue(stillUnrestricted.castlePiecesReady(color, side));
            }
        }
    }

    @Test
    public void testMoveToSquare() {
        // We only test that moving *to* the white king rook square
        // restricts castling in the same way that moving
        // *from* the white king rook square does.
        // If that's the case, then we can assume the other
        // squares are handled correctly, as well.

        ChessMove toKingRookSquare =  new ChessMove("d5", "h1");
        CastlingInfo whiteCannotKingCastle = CastlingInfo.allowAll().updated(toKingRookSquare);
        assertFalse(whiteCannotKingCastle.castlePiecesReady(WHITE, KINGSIDE));
        assertTrue(whiteCannotKingCastle.castlePiecesReady(WHITE, QUEENSIDE));
        assertTrue(whiteCannotKingCastle.castlePiecesReady(BLACK, KINGSIDE));
        assertTrue(whiteCannotKingCastle.castlePiecesReady(BLACK, QUEENSIDE));
    }

    @Test
    public void testEquality() {
        CastlingInfo base = CastlingInfo.allowAll();
        // Test equality.
        CastlingInfo whiteCannotKingCastle = base.updated(new ChessMove("h1", "h2"));
        CastlingInfo whiteCannotKingCastle2 = base.updated(new ChessMove("d5", "h1"));
        assertEquals(whiteCannotKingCastle, whiteCannotKingCastle2);

        // Test hashCode.
        assertEquals(whiteCannotKingCastle.hashCode(), whiteCannotKingCastle2.hashCode());

        // Test *sameness,* i.e. that two equal CastlingInfos, obtained differently,
        // refer to the exact same (cached) object.
        assertSame(whiteCannotKingCastle, whiteCannotKingCastle2);

        // Test inequality.
        assertFalse(base.equals(whiteCannotKingCastle));
    }
}
