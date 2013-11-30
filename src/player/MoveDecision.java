package player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.Move;

public class MoveDecision {
    private final List<Move> moveList;
    private final float score;
    
    public MoveDecision(List<Move> moveList, float score) {
        this.moveList = Collections.unmodifiableList(moveList);
        this.score = score;
    }

    public List<Move> getMoveList() {
        return moveList;
    }
    
    public Move getFirstMove() {
        return moveList.get(0);
    }
    
    public float getScore() {
        return score;
    }

    public static MoveDecision highestScored(Collection<MoveDecision> decisions, boolean reverse) {
        assert decisions.size() > 0;
        
        final float MULTIPLIER = reverse ? -1.0f : +1.0f;
        
        boolean seenAny = false;
        // Optimal post-multiplication score.
        float optimalScore = 0.0f;
        MoveDecision best = null;

        float currentScore; 
        for (MoveDecision d : decisions) {
            currentScore = d.getScore() * MULTIPLIER;
            if (!seenAny || currentScore > optimalScore ) {
                best = d;
                optimalScore = currentScore;
            }
        }
        return best;
    }
    
    public static MoveDecision highestScored(Collection<MoveDecision> decisions) {
        return highestScored(decisions, false);
    }
    
    public static MoveDecision lowestScored(Collection<MoveDecision> decisions) {
        return highestScored(decisions, true);
    }
    
}
