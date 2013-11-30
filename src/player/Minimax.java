package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chess.Board;
import chess.Move;
import chess.Piece;
import exceptions.InvalidMoveException;

public class Minimax {
    public static MoveDecision bestMove(Board board, int depth) {
        List<Move> nextMoves = new ArrayList<Move>();
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative.");
        } else if (depth == 0) {
            return new MoveDecision(nextMoves, Heuristic.pieceValueHeuristic(board));
        } else {
            // Get all possible decisions.
            List<MoveDecision> possibleDecisions = new ArrayList<MoveDecision>();
            Board possibleResult;
            List<Move> legalMoves = new ArrayList<Move>(board.legalMoves());
            if (legalMoves.size() == 0) {
                // You're checkmated, or stalemated.
                if (board.checked(board.getToMoveColor())) {
                    // Checkmated.
                    // TODO(jasonpr): Track mate in 1 vs mate in 2, etc.
                    float mateScore;
                    if (board.getToMoveColor() == Piece.PieceColor.WHITE) {
                        mateScore = -10000.0f;
                    } else {
                        mateScore = +10000.0f;
                    }
                    return new MoveDecision(nextMoves, mateScore);
                } else {
                    // Stalemated.
                    // TODO(jasonpr): Decide whether to explicitly define stalemate.
                    return new MoveDecision(nextMoves, 0.0f);
                }
            }
            
            Collections.shuffle(legalMoves);
            for (Move m : legalMoves) {
                try {
                    possibleResult = board.moveResult(m);
                } catch (InvalidMoveException e) {
                    throw new RuntimeException();
                }
                MoveDecision nextDecision = bestMove(possibleResult, depth - 1);
                nextMoves = new ArrayList<Move>();
                nextMoves.add(m);
                nextMoves.addAll(nextDecision.getMoveList());
                possibleDecisions.add(new MoveDecision(nextMoves, nextDecision.getScore()));
            }
            
            // Choose the possible decision that give the optimal score.
            MoveDecision bestDecision;
            if (board.getToMoveColor() == Piece.PieceColor.WHITE){
                bestDecision = MoveDecision.highestScored(possibleDecisions);
            } else {
                bestDecision = MoveDecision.lowestScored(possibleDecisions);
            }
            return bestDecision;
        }
    }   
}
