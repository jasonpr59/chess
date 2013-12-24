package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlphaBeta<P extends Position<P>> extends AbstractDecider<P>{
    
    private static final float EXTENSION_THRESHOLD = 0.7f;
    private final Heuristic<P> heuristic;
    
    public AlphaBeta(Heuristic<P> heuristic) {
        this.heuristic = heuristic;
    }
    
    @Override
    public Decision<P> bestDecision(P state, int depth) {
        // TODO(jasonpr): Come up with a better fake parent score.
        return alphaBeta(state, depth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0.0f);
    }   
    
    private Decision<P> alphaBeta(P position, int depth, float alpha, float beta, float parentScore) {
        float score = heuristic.value(position);
        if (depth > 0 || shouldExtend(score, parentScore)) {
            // Generate all legal transitions.
            List<Move<P>> moves = new ArrayList<Move<P>>(position.moves());
            // TODO(jasonpr): Order nicely.
            Collections.shuffle(moves);
            
            // Decide it's checkmate/stalemate.
            if (moves.size() == 0) {
                return AbstractDecider.terminalDecision(position);
            }
            final boolean isMaxStep = position.shouldMaximize(); 
            final float mult = isMaxStep? 1.0f : -1.0f;
            
            // We'll never actually return null:
            // bestDecision is ALWAYS set in the legalMoves loop.
            // (since we've gotten this far, legalMoves.size() >= 1.)
            Decision<P> bestDecision = null;
            boolean seenAny = false;
            P possibleResult;
            List<Move<P>> variation = new ArrayList<Move<P>>();
            for (Move<P> t : moves) {
                // Get the result, so we can do alphaBeta recursively.
                possibleResult = t.result(position);

                // Get the best decision from this possible result...
                Decision<P> nextDecision = alphaBeta(possibleResult, depth - 1, alpha, beta, score);
                if (!seenAny || nextDecision.getScore() * mult > bestDecision.getScore() * mult) {
                    seenAny = true;
                    variation = new ArrayList<Move<P>>();
                    variation.add(t);
                    variation.addAll(nextDecision.getVariation());
                    bestDecision = new Decision<P>(variation, nextDecision.getScore());
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
    
    /**
     * Return whether to search deeper, even once the required depth has been reached.
     * If the score changed drastically in the last Move, there might be a response that
     * will swing the score back in the opposite direction.  So, if there was a drastic
     * score change, we return "true, search deeper," so that we can continue searching
     * until the drastic moves stop coming.  At that point, the dust has settled, and
     * there's a better chance that the Position's score is an accurate representation
     * of the Position's value.
     * 
     * @param score The Position's current score.
     * @param parentScore The score of the Position that led to this one.
     */
    private static boolean shouldExtend(float score, float parentScore) {
        return Math.abs(score - parentScore) > EXTENSION_THRESHOLD;
    }
}
