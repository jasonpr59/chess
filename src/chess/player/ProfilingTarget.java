package chess.player;

import player.AlphaBeta;
import player.Decider;
import player.Heuristic;
import chess.ChessPosition;
import chess.ChessPositionBuilder;

public class ProfilingTarget {

    private static final int RUNS = 20;

    public static void main(String[] args) {
        // Just run some AlphaBeta, so the profiler can do its thing.
        Heuristic<ChessPosition> heuristic = new BoardPieceValueHeuristic();
        Decider<ChessPosition> decider = new AlphaBeta<ChessPosition>(heuristic);

        long startTime = System.currentTimeMillis();
        for (int run = 0; run < RUNS; run++){
            decider.bestDecision(new ChessPositionBuilder().setupNewGame().build(), 4);
        }
        long endTime = System.currentTimeMillis();
        long runTime = endTime- startTime;
        System.out.println("Took " + runTime + "ms for " + RUNS + " runs.");
    }
}
