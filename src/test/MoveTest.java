package test;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import chess.Game;
import chess.ChessMove;
import chess.Square;
import exceptions.AlgebraicNotationException;
import exceptions.InvalidMoveException;

public class MoveTest {
    @Test
    public void testDoublePawnPush() throws InvalidMoveException, AlgebraicNotationException{
        String[] moves = {"e4", "h6"};
        Game g = Game.fromMoves(moves);
        
        ChessMove illegalDoublePush = new ChessMove(Square.algebraic("e4"),
                                          Square.algebraic("e6"));
        assertFalse(illegalDoublePush.isLegal(g.getBoard()));
        
    }
}
