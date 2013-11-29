package chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import exceptions.InvalidMoveException;

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
    
    @Test
    public void testLegalMoves() throws InvalidMoveException {
        Board b = Board.newGame();
        // 1. e3
        Move m1a = new Move(Square.algebraic("e2"), Square.algebraic("e3"));
        // ... d6
        Move m1b = new Move(Square.algebraic("d7"), Square.algebraic("d6"));
        // 2. Bb5#
        Move m2a = new Move(Square.algebraic("f1"), Square.algebraic("b5"));
        // 2. ... Bd7 is legal.
        Move m2b_legal = new Move(Square.algebraic("c8"), Square.algebraic("d7"));
        // 2. ... h6 is illegal, because it would leave the Black king in check.
        Move m2b_illegal = new Move(Square.algebraic("h7"), Square.algebraic("h6"));
       
        
        Board blackChecked = b.moveResult(m1a).moveResult(m1b).moveResult(m2a);

        // The legal response should be playable with no issue:
        blackChecked.moveResult(m2b_legal);
        
        // The illegal response should throw an 
        try {
            blackChecked.moveResult(m2b_illegal);
            fail();
        } catch (InvalidMoveException e) {
            // Good!  This move *was* invalid.
            // Swallow the exception.
        }
    }

}
