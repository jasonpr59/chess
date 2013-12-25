package server;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import player.AlphaBeta;
import player.Decider;
import player.Decision;
import player.Heuristic;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.player.BoardPieceValueHeuristic;

public class StreamChessServer {

    private final InputStream in;
    private final PrintStream out;

    private ChessPosition position;
    private Heuristic<ChessPosition> heuristic;
    private Decider<ChessPosition> decider;

    // The depth to which the Decider should search.
    private static final int DEPTH = 3 /* plies */;

    public StreamChessServer(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;

        // Setup the board.
        position = new ChessPositionBuilder().setupNewGame().build();

        // Setup the player.
        heuristic = new BoardPieceValueHeuristic();
        decider = new AlphaBeta<ChessPosition>(heuristic);
    }

    private void error(String msg) {
        final String ERROR_TOKEN = "ERROR";
        out.println(ERROR_TOKEN + ": " + msg);
    }

    private void handleInput(String input) {
        // Get the move from the input.
        ChessMove m;
        try {
            m = ChessMove.deserialized(input);
        } catch (ArrayIndexOutOfBoundsException e) {
            error("cannot deserialize");
            return;
        }

        // Play the move on the board.
        if (m.isLegal(position)) {
            position = m.result(position);
        } else {
            error("illegal move");
            return;
        }

        // Decide a response, and play it on the board.
        Decision<ChessPosition> bestDecision = decider.bestDecision(position, DEPTH);
        ChessMove response = (ChessMove) bestDecision.getFirstMove();
        position  = response.result(position);

        // Print the move.
        out.println(response.serialized());
    }

    public void run() {
        Scanner sc = new Scanner(in);
        String input;
        while ((input = sc.next()) != "") {
            handleInput(input);
        }
    }

    public static void main(String[] args) {
        new StreamChessServer(System.in, System.out).run();
    }
}
