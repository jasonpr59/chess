package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import chess.ChessMove;
import chess.ChessPosition;
import chess.Game;
import chess.Square;
import chess.exceptions.AlgebraicNotationException;
import chess.exceptions.IllegalMoveException;

public class BoardTest {
    @Test
    public void testLegalMoves() throws IllegalMoveException, AlgebraicNotationException {
        String[] moves = {"e3", "d6",
                          "Bb5"};

        Game g = Game.fromMoves(moves);

        ChessPosition blackChecked = g.getCurrentPosition();

        ChessMove legal = new ChessMove(Square.algebraic("c8"), Square.algebraic("d7"));
        assertTrue(legal.isLegal(blackChecked));

        // 2. ... h6 is illegal, because it would leave the Black king in check.
        ChessMove illegal = new ChessMove(Square.algebraic("h7"), Square.algebraic("h6"));
        assertFalse(illegal.isLegal(blackChecked));

        assertEquals(5, blackChecked.moves().size());
    }
}
