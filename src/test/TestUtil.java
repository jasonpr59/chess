package test;

import static org.junit.Assert.*;

import java.util.Collection;

import chess.CastlingInfo;
import chess.ChessPosition;
import chess.Piece;
import chess.Square;

/** Globally useful utility functions for testing. */
public class TestUtil {
    public static <T> void assertSameElements(Collection<T> expected, Collection<T> actual) {
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    /** Assert that a ChessPosition is in the new-game position. */
    public static void assertIsNewGame(ChessPosition position) {
        // Do a couple perfunctory spot checks for pieces.
        Piece whiteBishop = new Piece(Piece.Type.BISHOP, Piece.Color.WHITE);
        Piece c1Piece = position.getPiece(Square.algebraic("c1"));
        assertEquals(whiteBishop, c1Piece);

        Piece blackPawn = new Piece(Piece.Type.PAWN, Piece.Color.BLACK);
        Piece f7Piece = position.getPiece(Square.algebraic("f7"));
        assertEquals(blackPawn, f7Piece);

        // Make sure that all the middle squares are empty.
        for (int file = 1; file <= 8; file++) {
            for (int rank = 3; rank <= 6; rank++) {
                assertNull(position.getPiece(Square.squareAt(file, rank)));
            }
        }

        // Assert other info is correct.
        assertNull(position.getEnPassantSquare());
        assertEquals(Piece.Color.WHITE, position.getToMoveColor());
        assertEquals(CastlingInfo.allowAll(), position.getCastlingInfo());
    }
}
