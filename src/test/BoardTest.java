package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import chess.Board;
import chess.Move;
import chess.Square;
import exceptions.InvalidMoveException;

public class BoardTest {
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

        // The illegal response should throw an exception
        try {
            blackChecked.moveResult(m2b_illegal);
            fail();
        } catch (InvalidMoveException expected) {
        }

        assertEquals(5, blackChecked.legalMoves().size());
    }
}
