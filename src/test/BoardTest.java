package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import chess.ChessPosition;
import chess.ChessMove;
import chess.Square;
import chess.exceptions.IllegalMoveException;

public class BoardTest {
    @Test
    public void testLegalMoves() throws IllegalMoveException {
        ChessPosition b = ChessPosition.newGame();
        // 1. e3
        ChessMove m1a = new ChessMove(Square.algebraic("e2"), Square.algebraic("e3"));
        // ... d6
        ChessMove m1b = new ChessMove(Square.algebraic("d7"), Square.algebraic("d6"));
        // 2. Bb5#
        ChessMove m2a = new ChessMove(Square.algebraic("f1"), Square.algebraic("b5"));
        // 2. ... Bd7 is legal.
        ChessMove m2b_legal = new ChessMove(Square.algebraic("c8"), Square.algebraic("d7"));
        // 2. ... h6 is illegal, because it would leave the Black king in check.
        ChessMove m2b_illegal = new ChessMove(Square.algebraic("h7"), Square.algebraic("h6"));

        ChessPosition blackChecked = b.moveResult(m1a).moveResult(m1b).moveResult(m2a);

        // The legal response should be playable with no issue:
        blackChecked.moveResult(m2b_legal);

        // The illegal response should throw an exception
        assertFalse(m2b_illegal.isLegal(blackChecked));

        assertEquals(5, blackChecked.legalMoves().size());
    }
}
