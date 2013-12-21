package server;

import java.util.Scanner;

import player.AlphaBeta;
import player.MoveDecision;
import chess.Board;
import chess.Move;

public class StandardStreamChessServer {
    
    private static final String ERROR_TOKEN = "ERROR";
    private static final int DEPTH = 3 /* plies */;
    
    public static void main(String[] args) {
        // Setup the board.
        Board board = Board.newGame();
        
        Scanner sc = new Scanner(System.in);
        String input;
        Move m;
        // TODO(jasonpr): Make sure this is the right way to loop
        // on Scanner input.
        while ((input = sc.next()) != null) {
            // Get move from input line.
            try {
                m = Move.deserialized(input);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(ERROR_TOKEN);
                continue;
            }
            
            // Play it on the board.
            if (m.isLegal(board)) {
                board = board.moveResult(m);
            } else {
                System.out.println(ERROR_TOKEN);
                continue;
            }
            
            // Decide a response.
            MoveDecision bestDecision = AlphaBeta.bestMove(board, DEPTH); 
            m = bestDecision.getFirstMove();
            
            // Play the response on the board.
            board = board.moveResult(m);

            // Print the move.
            System.out.println(m.serialized());
        }
    }
}
