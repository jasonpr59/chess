package test.chess;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import chess.NormalChessMove;
import chess.Game;
import chess.Square;
import chess.exceptions.AlgebraicNotationException;
import chess.exceptions.IllegalMoveException;

public class ChessMoveTest {
    @Test
    public void testDoublePawnPush() throws IllegalMoveException, AlgebraicNotationException{
        String[] moves = {"e4", "h6"};
        Game g = Game.fromMoves(moves);

        NormalChessMove illegalDoublePush = new NormalChessMove(Square.algebraic("e4"),
                                          Square.algebraic("e6"));
        assertFalse(illegalDoublePush.isLegal(g.getCurrentPosition()));
    }
    @Test
    public void test() {
        fail("Not yet fully implemented.");
    }
}
