package test.chess;

import static org.junit.Assert.fail;

import org.junit.Test;

import chess.Game;
import chess.exceptions.AlgebraicNotationException;
import chess.exceptions.IllegalMoveException;

public class GameTest {
    @Test
    public void testGame() throws IllegalMoveException, AlgebraicNotationException {
        // Fried liver attack!
        // Used because it's somewhat varied:
        // Different pieces, different captures,
        // checks.
        String[] moves = {"e4", "e5",
                          "Nf3", "Nc6",
                          "Bc4", "Nf6",
                          "Ng5", "d5",
                          "exd5", "Nxd5",
                          "Nxf7", "Kxf7",
                          // TODO(jasonpr): Indicate Check ("Qf3+") once it's implemented!
                          "Qf3"};
        // Ensure that this executes without error.
        Game.fromMoves(moves);
    }

    @Test
    public void test() {
        fail("Not yet fully implemented.");
    }
}
