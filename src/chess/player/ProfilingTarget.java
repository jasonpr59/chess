package chess.player;

import player.Decider;
import player.Heuristic;
import player.Minimax;
import chess.ChessPosition;

public class ProfilingTarget {

    public static void main(String[] args) {
        // Just run some minimax, so the profiler can do its thing.
        Heuristic<ChessPosition> heuristic = new BoardPieceValueHeuristic();
        Decider<ChessPosition> decider = new Minimax<ChessPosition>(heuristic);
        decider.bestDecision(ChessPosition.newGame(), 3);
        System.out.println("Done.");
    }
    
}