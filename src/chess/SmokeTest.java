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

        assertEquals(5, blackChecked.legalMoves().size());
    }
    
    @Test
    public void testKingCastling() throws InvalidMoveException, AlgebraicNotationException {
        // Get in castle-able position.
        String[] moves = {"e4", "e5",
                          "Bc4", "Nf6",
                          "Nf3", "Nc6"};
        Game g = Game.fromMoves(moves);

        // Castle
        Board b_good = g.getBoard().moveResult(new Move(Square.algebraic("e1"), Square.algebraic("g1")));
        
        // Be sure pieces moved around correctly.
        assertEquals(b_good.getPiece(Square.algebraic("e1")), null);
        assertEquals(b_good.getPiece(Square.algebraic("f1")), new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE));        
        assertEquals(b_good.getPiece(Square.algebraic("g1")), new Piece(Piece.PieceType.KING, Piece.PieceColor.WHITE));
        assertEquals(b_good.getPiece(Square.algebraic("h1")), null);
                
        // Be sure black can't castle through his bishop! 
        try {
            Board b_bad = b_good.moveResult(new Move(Square.algebraic("e8"), Square.algebraic("g8")));
            fail("Successfully castled, when castling should have been impossible!");
        } catch (InvalidMoveException e) {
            // Good, it *was* an invalid move!
        }
    }
    
    @Test
    public void testQueenCastling() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"d3", "d6",
                          "Be3", "Be6",
                          "Nc3", "Qd7",
                          "Qd2", "Nf6"};
        Game g = Game.fromMoves(moves);
        Board b_good = g.getBoard().moveResult(new Move(Square.algebraic("e1"), Square.algebraic("c1")));
        
        // Be sure pieces moved around correctly.
        assertEquals(b_good.getPiece(Square.algebraic("a1")), null);
        assertEquals(b_good.getPiece(Square.algebraic("c1")), new Piece(Piece.PieceType.KING, Piece.PieceColor.WHITE));
        assertEquals(b_good.getPiece(Square.algebraic("d1")), new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE));        
        assertEquals(b_good.getPiece(Square.algebraic("e1")), null);
        
        // Be sure black can't castle through his knight.
        try {
            Board b_bad = b_good.moveResult(new Move(Square.algebraic("e8"), Square.algebraic("c8")));
            fail("Successfully castled, when castling should have been impossible!");
        } catch (InvalidMoveException e) {
            // Good, it *was* an invalid move!
        }
    }
    
    @Test
    public void testNoCastleThroughCheck() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"Nf3", "Nf6",
                          "g3", "b6",
                          "Bg2", "Ba6",
                          "e3", "g6"};
        Game g = Game.fromMoves(moves);
        // Be sure white can't castle (because the bishop on a6 attacks f1).
        try {
            Board b_bad = g.getBoard().moveResult(new Move(Square.algebraic("e1"), Square.algebraic("g1")));
            fail();
        } catch (InvalidMoveException e) {
            // Good, it *was* an invalid move!
        }
    }

    @Test
    public void testNoCastleOutOfCheck() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"e4", "e5",
                          "d4", "Nf6",
                          "Nf3", "Nc6",
                          "Bb5", "Bb4"};
        Game g = Game.fromMoves(moves);
        
       try{
           Board b_bad = g.getBoard().moveResult(new Move(Square.algebraic("e1"), Square.algebraic("g1")));
           fail("Succeeded in castling out of check.");
       } catch (InvalidMoveException e) {
           // Good, it *was* an invalid move!
       }
    }
    
    
    
    
    public void testGame() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"e4", "e5",
                          "Nf3", "Nc6",
                          "Bc4", "Nf6",
                          "Ng5", "d5",
                          "exd5", "Nxd5",
                          "Nxf7", "Kxf7",
                          // TODO(jasonpr): Indicate Check ("Qf3+") once it's implemented!
                          "Qf3"};
        Game g = Game.fromMoves(moves);
    }
}
