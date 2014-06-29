package com.stalepretzel.chess.piece;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.stalepretzel.chess.piece.King;
import com.stalepretzel.chess.piece.Piece;
import com.stalepretzel.chess.piece.Rook;

/** Tests for the chess.Piece class. */
public class PieceTest {

    @Test
    public void testGetters() {
        Piece whiteRook = new Rook(Piece.Color.WHITE);
        assertEquals(Piece.Color.WHITE, whiteRook.getColor());
    }

    @Test
    public void testEquality() {
        Piece whiteRook = new Rook(Piece.Color.WHITE);
        Piece blackRook = new Rook(Piece.Color.BLACK);
        Piece whiteKing = new King(Piece.Color.WHITE);
        Piece otherWhiteRook = new Rook(Piece.Color.WHITE);

        assertEquals(whiteRook, otherWhiteRook);
        assertEquals(whiteRook.hashCode(), otherWhiteRook.hashCode());

        assertFalse(whiteRook.equals(blackRook));
        assertFalse(whiteRook.equals(whiteKing));
    }

    @Test
    public void testColorOpposite() {
        assertEquals(Piece.Color.BLACK, Piece.Color.WHITE.opposite());
        assertEquals(Piece.Color.WHITE, Piece.Color.BLACK.opposite());
    }
}
