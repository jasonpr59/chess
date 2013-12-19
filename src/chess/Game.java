package chess;

import java.util.ArrayList;
import java.util.List;

import exceptions.AlgebraicNotationException;
import exceptions.InvalidMoveException;

public class Game {
    private final List<Board> history;
    
    public Game() {
        history = new ArrayList<Board>();
        history.add(Board.newGame());
    }
    
    public Game makeMove(Move move) throws InvalidMoveException{
        history.add(getBoard().moveResult(move));
        return this;
    }

    public Board getBoard() {
        return history.get(history.size() - 1);
    }

    /**
     * Get the position arrived at AFTER playing a move.
     * @param move The number of the move, in 1, 2, 3, ...
     * @param ply 1 for white's ply, 2 for black's ply.
     * @return The Board at the given point in the game.
     */
    public Board getBoard(int move, int ply) {
        if (move < 0) {
            throw new IllegalArgumentException("Move cannot be negative.");
        } else if (move == 0) {
            // The zero'th  move is just the start of the game.
            return history.get(0);
        } else {
            if (ply != 1 && ply != 2) {
                throw new IllegalArgumentException("Ply must be 1 or 2.");
            }
            return history.get(2 * (move - 1) + ply);
        }
    }
    
    
    public static Game fromMoves(String[] moves) throws AlgebraicNotationException, InvalidMoveException {
        Game g = new Game();
        for (String move : moves) {
            g.makeMove(AlgebraicParser.parseAlgebraic(move, g.getBoard()));
        }
        return g;
    }
}
