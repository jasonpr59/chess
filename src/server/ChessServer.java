package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import player.AlphaBeta;
import player.Heuristic;
import player.MoveDecision;
import chess.AlgebraicParser;
import chess.Board;
import chess.Move;
import chess.Piece;
import chess.Square;
import exceptions.InvalidMoveException;

public class ChessServer {
    public static void main(String[] args) throws IOException {
        int portNumber = 4444;
        ServerSocket listener = new ServerSocket(portNumber);
        Socket handler = listener.accept();
        PrintWriter out =
                new PrintWriter(handler.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(handler.getInputStream()));

        Board board = Board.newGame();

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            Move m = AlgebraicParser.parseAlgebraic(inputLine, board);
            try {
                board = board.moveResult(m);
            } catch (InvalidMoveException e) {
                out.println("Invalid move!");
            }
            out.println("Board");
            out.println(colorize(board));
            out.println("H(board) = " + Heuristic.pieceValueHeuristic(board));
            MoveDecision bestDecision = AlphaBeta.bestMove(board, 3);
            out.println("H(continuation) = " + bestDecision.getScore());
            out.println("In view of :" + Arrays.toString(bestDecision.getMoveList().toArray()));
        }
    }

    private static String colorize(Board board) {
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
                    switch (p.getPieceColor()) {
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
