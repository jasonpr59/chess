package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import chess.Board;
import chess.Game;
import chess.Move;
import chess.Piece;
import chess.Square;
import exceptions.AlgebraicNotationException;
import exceptions.InvalidMoveException;

public class CastlingTest {
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
    
    
    
    
}