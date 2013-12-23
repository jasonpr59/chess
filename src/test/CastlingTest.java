package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import chess.ChessPosition;
import chess.Game;
import chess.ChessMove;
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
        ChessPosition b_good = g.getBoard().moveResult(new ChessMove(Square.algebraic("e1"), Square.algebraic("g1")));
        
        // Be sure pieces moved around correctly.
        assertEquals(b_good.getPiece(Square.algebraic("e1")), null);
        assertEquals(b_good.getPiece(Square.algebraic("f1")), new Piece(Piece.Type.ROOK, Piece.Color.WHITE));        
        assertEquals(b_good.getPiece(Square.algebraic("g1")), new Piece(Piece.Type.KING, Piece.Color.WHITE));
        assertEquals(b_good.getPiece(Square.algebraic("h1")), null);
                
        // Be sure black can't castle through his bishop! 
        ChessMove illegalCastle = new ChessMove(Square.algebraic("e8"), Square.algebraic("g8"));
        assertFalse(illegalCastle.isLegal(b_good));
    }
    
    @Test
    public void testQueenCastling() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"d3", "d6",
                          "Be3", "Be6",
                          "Nc3", "Qd7",
                          "Qd2", "Nf6"};
        Game g = Game.fromMoves(moves);
        ChessPosition b_good = g.getBoard().moveResult(new ChessMove(Square.algebraic("e1"), Square.algebraic("c1")));
        
        // Be sure pieces moved around correctly.
        assertEquals(b_good.getPiece(Square.algebraic("a1")), null);
        assertEquals(b_good.getPiece(Square.algebraic("c1")), new Piece(Piece.Type.KING, Piece.Color.WHITE));
        assertEquals(b_good.getPiece(Square.algebraic("d1")), new Piece(Piece.Type.ROOK, Piece.Color.WHITE));        
        assertEquals(b_good.getPiece(Square.algebraic("e1")), null);
        
        // Be sure black can't castle through his knight.
        ChessMove illegalCastle = new ChessMove(Square.algebraic("e8"), Square.algebraic("c8"));
        assertFalse(illegalCastle.isLegal(b_good));
    }
    
    @Test
    public void testNoCastleThroughCheck() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"Nf3", "Nf6",
                          "g3", "b6",
                          "Bg2", "Ba6",
                          "e3", "g6"};
        Game g = Game.fromMoves(moves);
        
        ChessMove illegalCastle = new ChessMove(Square.algebraic("e1"), Square.algebraic("g1"));
        assertFalse(illegalCastle.isLegal(g.getBoard()));
    }

    @Test
    public void testNoCastleOutOfCheck() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"e4", "e5",
                          "d4", "Nf6",
                          "Nf3", "Nc6",
                          "Bb5", "Bb4"};
        Game g = Game.fromMoves(moves);
        
        ChessMove illegalCastle = new ChessMove(Square.algebraic("e1"), Square.algebraic("g1"));
        assertFalse(illegalCastle.isLegal(g.getBoard()));
    }

    @Test
    public void testLegalKingCastle() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"e4", "e5",
                          "Nf3", "Nf6",
                          "Bc4", "Bc5",
                          "O-O", "O-O"};

        // For now, just make sure there are no exceptions.
        Game.fromMoves(moves);
    }
}
