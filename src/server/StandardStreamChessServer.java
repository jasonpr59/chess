package server;

import java.util.Scanner;

import player.AlphaBeta;
import player.Decider;
import player.Decision;
import player.Heuristic;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.player.BoardPieceValueHeuristic;

public class StandardStreamChessServer {
    
    private static final String ERROR_TOKEN = "ERROR";
    private static final int DEPTH = 3 /* plies */;
    
    public static void main(String[] args) {
        // Setup the board.
        ChessPosition board = new ChessPositionBuilder().setupNewGame().build();

        Heuristic<ChessPosition> heuristic = new BoardPieceValueHeuristic();
        Decider<ChessPosition> decider = new AlphaBeta<ChessPosition>(heuristic);
        
        Scanner sc = new Scanner(System.in);
        String input;
        ChessMove m;
        // TODO(jasonpr): Make sure this is the right way to loop
        // on Scanner input.
        while ((input = sc.next()) != null) {
            // Get move from input line.
            try {
                m = ChessMove.deserialized(input);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(ERROR_TOKEN + ": cannot deseralize.");
                continue;
            }
            
            // Play it on the board.
            if (m.isLegal(board)) {
                board = m.result(board);
            } else {
                System.out.println(ERROR_TOKEN + ": illegal move!");
                continue;
            }
            
            // Decide a response, and play it on the board.
            Decision<ChessPosition> bestDecision = decider.bestDecision(board, DEPTH); 
            board  = bestDecision.getFirstMove().result(board);
            
            // Print the move.
            System.out.println(m.serialized());
        }
    }
}
