package player;

import chess.ChessPosition;

public class ProfilingTarget {

    public static void main(String[] args) {
        // Just run some minimax, so the profiler can do its thing.
        Heuristic<ChessPosition> heuristic = new BoardPieceValueHeuristic();
        Minimax.bestMove(ChessPosition.newGame(), 3, heuristic);
        System.out.println("Done.");
    }
    
}
