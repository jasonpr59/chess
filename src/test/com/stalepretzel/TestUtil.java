package com.stalepretzel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import com.stalepretzel.chess.CastlingInfo;
import com.stalepretzel.chess.ChessPosition;
import com.stalepretzel.chess.Square;
import com.stalepretzel.chess.piece.Bishop;
import com.stalepretzel.chess.piece.Pawn;
import com.stalepretzel.chess.piece.Piece;

/** Globally useful utility functions for testing. */
public class TestUtil {
    public static <T> void assertSameElements(Iterable<T> expected, Iterable<T> actual) {
        Collection<T> allExpected = new ArrayList<T>();
        for (T t : expected) {
            allExpected.add(t);
        }

        Collection<T> allActual = new ArrayList<T>();
        for (T t : actual) {
            allActual.add(t);
        }

        assertTrue(allExpected.containsAll(allActual));
        assertTrue(allActual.containsAll(allExpected));
    }

    /** Assert that a ChessPosition is in the new-game position. */
    public static void assertIsNewGame(ChessPosition position) {
        // Do a couple perfunctory spot checks for pieces.
        Piece whiteBishop = new Bishop(Piece.Color.WHITE);
        Piece c1Piece = position.getPiece(Square.algebraic("c1"));
        assertEquals(whiteBishop, c1Piece);

        Piece blackPawn = new Pawn(Piece.Color.BLACK);
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
