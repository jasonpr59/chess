package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chess.Board;
import chess.Move;
import chess.Piece;
import exceptions.InvalidMoveException;

// TODO(jasonpr): Make this, and Minimax, share some Interface.
public class AlphaBeta {
    
    // TODO(jasonpr): Good god don't do this.
    private static final float INFINITY = 10000000;
    
    private static MoveDecision alphaBeta(Board board, int depth, float alpha, float beta) {
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative.");
        } else if (depth == 0) {
            return new MoveDecision(new ArrayList<Move>(), Heuristic.pieceValueHeuristic(board));
        } else {
            // Generate all legal moves.
            List<Move> legalMoves = new ArrayList<Move>(board.legalMoves());
            // TODO(jasonpr): Order nicely.
            Collections.shuffle(legalMoves);
            
            // Decide it's checkmate/stalemate.
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
                    return new MoveDecision(new ArrayList<Move>(), mateScore);
                } else {
                    // Stalemated.
                    // TODO(jasonpr): Decide whether to explicitly define stalemate.
                    return new MoveDecision(new ArrayList<Move>(), 0.0f);
                }
            }
            final boolean isMaxStep = board.getToMoveColor() == Piece.PieceColor.WHITE; 
            final float mult = isMaxStep? 1.0f : -1.0f;
            
            // We'll never actually return null:
            // bestDecision is ALWAYS set in the legalMoves loop.
            // (since we've gotten this far, legalMoves.size() >= 1.)
            MoveDecision bestDecision = null;
            boolean seenAny = false;
            Board possibleResult;
            List<Move> nextMoves = new ArrayList<Move>();
            for (Move m : legalMoves) {
                // Get the result, so we can do alphaBeta recursively.
                try {
                    possibleResult = board.moveResult(m);
                } catch (InvalidMoveException e) {
                    throw new RuntimeException();
                }
                // Get the best decision from this possible result...
                MoveDecision nextDecision = alphaBeta(possibleResult, depth - 1, alpha, beta);
                if (!seenAny || nextDecision.getScore() * mult > bestDecision.getScore() * mult) {
                    seenAny = true;
                    nextMoves = new ArrayList<Move>();
                    nextMoves.add(m);
                    nextMoves.addAll(nextDecision.getMoveList());
                    bestDecision = new MoveDecision(nextMoves, nextDecision.getScore());
                }
                
                // update alpha and beta
                if (isMaxStep && bestDecision.getScore() > alpha) { 
                    alpha = bestDecision.getScore();
                } else if (!isMaxStep && bestDecision.getScore() < beta) {
                    beta = bestDecision.getScore();
                }
                
                // ...and terminate if alpha-beta condition is satisfied.
                if (alpha >= beta) {
                    break;
                }
            }
            return bestDecision;
        }
    }
    
    public static MoveDecision bestMove(Board board, int depth) {
        return alphaBeta(board, depth, -INFINITY, INFINITY);
    }   
}
