package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import player.AlphaBeta;
import player.Decider;
import player.Decision;
import player.Heuristic;
import chess.AlgebraicParser;
import chess.ChessMove;
import chess.ChessPosition;
import chess.Piece;
import chess.Square;
import chess.player.BoardPieceValueHeuristic;
import exceptions.AlgebraicNotationException;

public class SocketChessServer {
    public static void main(String[] args) throws IOException {
        int portNumber = 4444;
        ServerSocket listener = new ServerSocket(portNumber);
        Socket handler = listener.accept();
        PrintWriter out =
                new PrintWriter(handler.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(handler.getInputStream()));

        ChessPosition board = ChessPosition.newGame();

        Heuristic<ChessPosition> heuristic = new BoardPieceValueHeuristic();
        Decider<ChessPosition> decider = new AlphaBeta<ChessPosition>();
        
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            ChessMove m;
            try {
                m = AlgebraicParser.parseAlgebraic(inputLine, board);
            } catch (AlgebraicNotationException e) {
                out.println("Invalid move!");
                continue;
            }
            if (m.isLegal(board)) {
                board = board.moveResult(m);
            } else {
                out.println("Invalid move!"); 
                continue;
            }
            
            out.println(colorize(board));
            out.println("H(board) = " + heuristic.value(board));

            out.println("Thinking...");
            Decision<ChessPosition> bestDecision = decider.bestMove(board, 3, heuristic);
            out.println("Making move" + bestDecision.getFirst());
            board = bestDecision.getFirst().result(board);
            
            out.println(colorize(board));
            out.println("H(continuation) = " + bestDecision.getScore());
            out.println("From sequence: " + Arrays.toString(bestDecision.getList().toArray()));
        }
    }

    private static String colorize(ChessPosition board) {
        StringBuilder sb = new StringBuilder();
        String pieceLetter;
        String colorCode;
        for (char rank = 8; rank >= 1; rank--) {
            for (char file = 1; file <= 8; file ++) {
                Piece p = board.getPiece(Square.squareAt(file, rank));
                if (p == null) {
                    sb.append(" ");
                } else {
                    switch (p.getType()) {
                        case PAWN:
                            pieceLetter = "P";
                            break;
                        case BISHOP:
                            pieceLetter = "B";
                            break;
                        case KING:
                            pieceLetter = "K";
                            break;
                        case KNIGHT:
                            pieceLetter = "N";
                            break;
                        case QUEEN:
                            pieceLetter = "Q";
                            break;
                        case ROOK:
                            pieceLetter = "R";
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                    switch (p.getColor()) {
                        case WHITE:
                            colorCode = "\033[92m";
                            break;
                        case BLACK:
                            colorCode = "\033[94m";
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                    sb.append(colorCode);
                    sb.append(pieceLetter);
                    sb.append("\033[0m");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
