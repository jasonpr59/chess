package player;

import chess.Board;

public class ProfilingTarget {

    public static void main(String[] args) {
        // Just run some minimax, so the profiler can do its thing.
        Minimax.bestMove(Board.newGame(), 3);
        System.out.println("Done.");
    }
    
}
