package test.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import chess.Piece;

/** Tests for the chess.Piece class. */
public class PieceTest {

    @Test
    public void testGetters() {
        Piece whiteRook = new Piece(Piece.Type.ROOK, Piece.Color.WHITE);
        assertEquals(Piece.Type.ROOK, whiteRook.getType());
        assertEquals(Piece.Color.WHITE, whiteRook.getColor());
    }

    @Test
    public void testEquality() {
        Piece whiteRook = new Piece(Piece.Type.ROOK, Piece.Color.WHITE);
        Piece blackRook = new Piece(Piece.Type.ROOK, Piece.Color.BLACK);
        Piece whiteKing = new Piece(Piece.Type.KING, Piece.Color.WHITE);
        Piece otherWhiteRook = new Piece(Piece.Type.ROOK, Piece.Color.WHITE);

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
