package player;

import chess.ChessPosition;

public class ProfilingTarget {

    public static void main(String[] args) {
        // Just run some minimax, so the profiler can do its thing.
        Heuristic<ChessPosition> heuristic = new BoardPieceValueHeuristic();
        Decider<ChessPosition> decider = new Minimax<ChessPosition>();
        decider.bestMove(ChessPosition.newGame(), 3, heuristic);
        System.out.println("Done.");
    }
    
}
