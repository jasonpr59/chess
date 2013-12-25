package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Minimax<P extends Position<P>> implements Decider<P>{
    
    private final Heuristic<P> heuristic;
    
    public Minimax(Heuristic<P> heuristic) {
        this.heuristic = heuristic;
    }
    
    @Override
    public Decision<P> bestDecision(P position, int depth) {
        List<Move<P>> nextTransitions = new ArrayList<Move<P>>();
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative.");
        } else if (depth == 0) {
            return new Decision<P>(nextTransitions, heuristic.value(position));
        } else {
            // Get all possible decisions.
            List<Decision<P>> possibleDecisions = new ArrayList<Decision<P>>();
            P possibleResult;
            List<Move<P>> transitions = new ArrayList<Move<P>>(position.moves());
            if (transitions.size() == 0) {
                return new Decision<P>(new ArrayList<Move<P>>(), heuristic.terminalValue(position));
            }
            
            Collections.shuffle(possibleDecisions);
            for (Decision<P> decision : possibleDecisions) {
                possibleResult = decision.getFirstMove().result(position);
                Decision<P> nextDecision = bestDecision(possibleResult, depth - 1);
                nextTransitions = new ArrayList<Move<P>>();
                nextTransitions.add(decision.getFirstMove());
                nextTransitions.addAll(nextDecision.getVariation());
                possibleDecisions.add(new Decision<P>(nextTransitions, nextDecision.getScore()));
            }
            
            // Choose the possible decision that give the optimal score.
            Decision<P> bestDecision;
            if (position.shouldMaximize()) {
                bestDecision = Decision.highestScored(possibleDecisions);
            } else {
                bestDecision = Decision.lowestScored(possibleDecisions);
            }
            return bestDecision;
        }
    }   
}
