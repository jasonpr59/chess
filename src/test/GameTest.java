package test;

import org.junit.Test;

import chess.Game;
import exceptions.AlgebraicNotationException;
import exceptions.InvalidMoveException;

public class GameTest {
    
    @Test
    public void testGame() throws InvalidMoveException, AlgebraicNotationException {
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
}
