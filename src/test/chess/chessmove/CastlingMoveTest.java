package test.chess.chessmove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import chess.CastlingMove;
import chess.ChessMove;
import chess.ChessPosition;
import chess.Game;
import chess.Square;
import chess.exceptions.AlgebraicNotationException;
import chess.exceptions.IllegalMoveException;
import chess.piece.King;
import chess.piece.Piece;
import chess.piece.Rook;

public class CastlingMoveTest {
    @Test
    public void testKingCastling() throws IllegalMoveException, AlgebraicNotationException {
        // Get in castle-able position.
        String[] moves = {"e4", "e5",
                          "Bc4", "Nf6",
                          "Nf3", "Nc6"};
        Game g = Game.fromMoves(moves);

        // Castle
        ChessMove castlingMove = new CastlingMove(Square.algebraic("e1"), Square.algebraic("g1"));
        assertTrue(castlingMove.isLegal(g.getCurrentPosition()));
        ChessPosition b_good = castlingMove.result(g.getCurrentPosition());

        // Be sure pieces moved around correctly.
        assertEquals(b_good.getPiece(Square.algebraic("e1")), null);
        assertEquals(b_good.getPiece(Square.algebraic("f1")), new Rook(Piece.Color.WHITE));
        assertEquals(b_good.getPiece(Square.algebraic("g1")), new King(Piece.Color.WHITE));
        assertEquals(b_good.getPiece(Square.algebraic("h1")), null);

        // Be sure black can't castle through his bishop!
        ChessMove illegalCastle = new CastlingMove(Square.algebraic("e8"), Square.algebraic("g8"));
        assertFalse(illegalCastle.isLegal(b_good));
    }

    @Test
    public void testQueenCastling() throws IllegalMoveException, AlgebraicNotationException {
        String[] moves = {"d3", "d6",
                          "Be3", "Be6",
                          "Nc3", "Qd7",
                          "Qd2", "Nf6"};
        Game g = Game.fromMoves(moves);
        ChessMove castlingMove = new CastlingMove(Square.algebraic("e1"), Square.algebraic("c1"));
        ChessPosition b_good = castlingMove.result(g.getCurrentPosition());

        // Be sure pieces moved around correctly.
        assertEquals(b_good.getPiece(Square.algebraic("a1")), null);
        assertEquals(b_good.getPiece(Square.algebraic("c1")), new King(Piece.Color.WHITE));
        assertEquals(b_good.getPiece(Square.algebraic("d1")), new Rook(Piece.Color.WHITE));
        assertEquals(b_good.getPiece(Square.algebraic("e1")), null);

        // Be sure black can't castle through his knight.
        ChessMove illegalCastle = new CastlingMove(Square.algebraic("e8"), Square.algebraic("c8"));
        assertFalse(illegalCastle.isLegal(b_good));
    }

    @Test
    public void testNoCastleThroughCheck() throws IllegalMoveException, AlgebraicNotationException {
        String[] moves = {"Nf3", "Nf6",
                          "g3", "b6",
                          "Bg2", "Ba6",
                          "e3", "g6"};
        Game g = Game.fromMoves(moves);

        ChessMove illegalCastle = new CastlingMove(Square.algebraic("e1"), Square.algebraic("g1"));
        assertFalse(illegalCastle.isLegal(g.getCurrentPosition()));
    }

    @Test
    public void testNoCastleOutOfCheck() throws IllegalMoveException, AlgebraicNotationException {
        String[] moves = {"e4", "e5",
                          "d4", "Nf6",
                          "Nf3", "Nc6",
                          "Bb5", "Bb4"};
        Game g = Game.fromMoves(moves);

        ChessMove illegalCastle = new CastlingMove(Square.algebraic("e1"), Square.algebraic("g1"));
        assertFalse(illegalCastle.isLegal(g.getCurrentPosition()));
    }

    @Test
    public void testLegalKingCastle() throws IllegalMoveException, AlgebraicNotationException {
        String[] moves = {"e4", "e5",
                          "Nf3", "Nf6",
                          "Bc4", "Bc5",
                          "O-O", "O-O"};

        // For now, just make sure there are no exceptions.
        Game.fromMoves(moves);
    }

    @Test
    public void test() {
        fail("Not yet fully implemented.");
    }
}
