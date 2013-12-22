package chess;

import java.util.ArrayList;
import java.util.List;

import exceptions.AlgebraicNotationException;
import exceptions.InvalidMoveException;
/** An entire chess game, starting from the initial position. */
public class Game {

    private final List<Board> history;

    /**
     * Construct a new Game.
     * At this point, only the initial Board position is in the
     * Game's history: no Moves have been made.
     */
    public Game() {
        history = new ArrayList<Board>();
        history.add(Board.newGame());
    }

    /**
     * Make a Move.
     * The Move is made on the current (last) board, and the
     * result is added to the history. This turns the result
     * into the new current board.
     * @return This Game, for daisy chaining.
     * @throws InvalidMoveException
     */
    public Game makeMove(Move move) throws InvalidMoveException{
        history.add(getBoard().moveResult(move));
        return this;
    }

    /** Get the current (most recent) Board. */
    public Board getBoard() {
        return history.get(history.size() - 1);
    }

    /**
     * Get the position arrived at AFTER playing a move.
     * @param move The number of the move, in 1, 2, 3, ...
     *     Or, 0, for the initial position.
     * @param ply 1 for white's ply, 2 for black's ply.
     *     If move == 0, then ply can be anything-- the initial
     *     position will be returned no matter what.
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

    /** Create a Game whose history matches a history in Algebraic notation. */
    public static Game fromMoves(String[] moves) throws AlgebraicNotationException, InvalidMoveException {
        Game g = new Game();
        for (String move : moves) {
            Move m = AlgebraicParser.parseAlgebraic(move, g.getBoard());
            if (m.isLegal(g.getBoard())){
                g.makeMove(m);
            } else {
                throw new InvalidMoveException();
            }
        }
        return g;
    }
}
