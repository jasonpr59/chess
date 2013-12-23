package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlphaBeta<P extends Position<P>> implements Decider<P>{
    
    private static final float EXTENSION_THRESHOLD = 0.7f;
    
    @Override
    public Decision<P> bestMove(P state, int depth, Heuristic<P> heuristic) {
        // TODO(jasonpr): Come up with a better fake parent score.
        return alphaBeta(state, depth, heuristic, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.0f);
    }   
    
    private Decision<P> alphaBeta(P position, int depth, Heuristic<P> heuristic, float alpha, float beta, float parentScore) {
        float score = heuristic.value(position);
        if (depth > 0 || shouldExtend(score, parentScore)) {
            // Generate all legal transitions.
            List<Move<P>> transitions = new ArrayList<Move<P>>(position.transitions());
            // TODO(jasonpr): Order nicely.
            Collections.shuffle(transitions);
            
            // Decide it's checkmate/stalemate.
            if (transitions.size() == 0) {
                // Any game is either a win, a loss, or a tie if there are no
                // legal transitions left.
                
                Outcome result = position.outcome();

                float outcomeScore;
                switch (result) {
                case WIN:
                    // Never the case: if you have no moves, you are
                    // either checkmated or stalemated.
                    throw new AssertionError("You never win if you have no moves!");
                case DRAW:
                    // TODO: Decide whether to explicitly define a Draw Score.
                    outcomeScore = 0.0f;
                    break;
                case LOSS:
                    // FIXME: This seems overly hacky.
                    // This could probably be fixed by making *every* step
                    // a "maximizing step," and just negating scores in each
                    // recursive call.
                    if (position.shouldMaximize()) {
                        // The maximizing player lost.
                        // TODO: Differentiate between "lose now" and "lose in x moves."
                        outcomeScore = -10000.0f;
                    } else {
                        outcomeScore = 10000.0f;
                    }
                    break;
                default:
                    throw new AssertionError("Result was not a valid value.");
                }
                return new Decision<P>(new ArrayList<Move<P>>(), outcomeScore);
            }
            final boolean isMaxStep = position.shouldMaximize(); 
            final float mult = isMaxStep? 1.0f : -1.0f;
            
            // We'll never actually return null:
            // bestDecision is ALWAYS set in the legalMoves loop.
            // (since we've gotten this far, legalMoves.size() >= 1.)
            Decision<P> bestDecision = null;
            boolean seenAny = false;
            P possibleResult;
            List<Move<P>> nextMoves = new ArrayList<Move<P>>();
            for (Move<P> t : transitions) {
                // Get the result, so we can do alphaBeta recursively.
                possibleResult = t.result(position);

                // Get the best decision from this possible result...
                Decision<P> nextDecision = alphaBeta(possibleResult, depth - 1, heuristic, alpha, beta, score);
                if (!seenAny || nextDecision.getScore() * mult > bestDecision.getScore() * mult) {
                    seenAny = true;
                    nextMoves = new ArrayList<Move<P>>();
                    nextMoves.add(t);
                    nextMoves.addAll(nextDecision.getVariation());
                    bestDecision = new Decision<P>(nextMoves, nextDecision.getScore());
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
        } else {
            return new Decision<P>(new ArrayList<Move<P>>(), score);
        }
    }
    
    private static boolean shouldExtend(float score, float parentScore) {
        return Math.abs(score - parentScore) > EXTENSION_THRESHOLD;
    }
}
