package chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

public class SmokeTest {

    @Test
    public void testSmokeTest() {
        Board b = Board.newGame();
        Square sq = Square.algebraic("b1");

        // Assert white knight at b1.
        assertEquals(b.getPiece(sq), new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE));
        
        // Assert the white knight at b1 can move to c3.
        Move m1 = new Move(sq, Square.algebraic("c3"));
        assertTrue(m1.isSane(b));
        
        // Assert that the white knight at b1 cannot move to c4
        Move m2 = new Move(sq, Square.algebraic("c4"));
        assertFalse(m2.isSane(b));
        
        // Assert that the white knight at b1 cannot move to d2.
        Move m3 = new Move(sq, Square.algebraic("d2"));
        assertFalse(m3.isSane(b));

        // Assert that there are 20 sane initial moves.
        Collection<Move> firstMoves = b.legalMoves();
        assertEquals(firstMoves.size(), 20);
    }

}
