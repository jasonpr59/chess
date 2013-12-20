package test;

import org.junit.Test;

import chess.Game;
import exceptions.AlgebraicNotationException;
import exceptions.InvalidMoveException;

public class GameTest {
    
    @Test
    public void testGame() throws InvalidMoveException, AlgebraicNotationException {
        String[] moves = {"e4", "e5",
                          "Nf3", "Nc6",
                          "Bc4", "Nf6",
                          "Ng5", "d5",
                          "exd5", "Nxd5",
                          "Nxf7", "Kxf7",
                          // TODO(jasonpr): Indicate Check ("Qf3+") once it's implemented!
                          "Qf3"};
        Game g = Game.fromMoves(moves);
    }
}
