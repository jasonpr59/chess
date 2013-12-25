package test;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import chess.Game;
import chess.ChessMove;
import chess.Square;
import chess.exceptions.AlgebraicNotationException;
import chess.exceptions.IllegalMoveException;

public class MoveTest {
    @Test
    public void testDoublePawnPush() throws IllegalMoveException, AlgebraicNotationException{
        String[] moves = {"e4", "h6"};
        Game g = Game.fromMoves(moves);
        
        ChessMove illegalDoublePush = new ChessMove(Square.algebraic("e4"),
                                          Square.algebraic("e6"));
        assertFalse(illegalDoublePush.isLegal(g.getCurrentPosition()));
        
    }
}
