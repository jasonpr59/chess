package player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An indication of which Transition is best to make.
 * In the context of adversarial search, the adversaries
 * take turns changing the game's State via Transitions.
 * @param <S> The type of State this decision is made for.
 */
public class Decision<S extends Position<S>> {
    private final List<Transition<S>> transitionList;
    private final float score;
    
    public Decision(List<Transition<S>> transitionList, float score) {
        this.transitionList = Collections.unmodifiableList(transitionList);
        this.score = score;
    }
    
    /**
     * Get the list of Transitions this Decision expects to make.
     * These Transitions lead toward a final state whose score
     * is this Decision's score.
     */
    public List<Transition<S>> getList() {
        return transitionList;
    }
    
    /**
     * Get the first Transition to make.
     * This Decision indicates that this Transition is the best
     * one to make, given the expected continuation.
     */
    public Transition<S> getFirst() {
        return transitionList.get(0);
    }
    
    /**
     * Return the expected score.
     * 
     * The expected score is the score of the State that will
     * result from optimal play for the depth explored to generate
     * this Decision, according to some heuristic.
     */
    public float getScore() {
        return score;
    }
    
    /**
     * Return the Decision with the best score.
     * The best score is the highest if highest is true.  Otherwise,
     * the best score is the lowest. 
     */
    public static <S extends Position<S>> Decision<S> bestScored(Collection<Decision<S>> decisions, boolean highestBest) {
        assert decisions.size() > 0;
        
        final float MULTIPLIER = highestBest ? +1.0f : -1.0f;
        
        boolean seenAny = false;
        // Optimal post-multiplication score.
        float optimalScore = 0.0f;
        Decision<S> best = null;

        float currentScore; 
        for (Decision<S> d : decisions) {
            currentScore = d.getScore() * MULTIPLIER;
            if (!seenAny || currentScore > optimalScore ) {
                best = d;
                optimalScore = currentScore;
                seenAny = true;
            }
        }
        return best;
    }
    
    /** Return the Decision with the highest score. */
    public static <S extends Position<S>> Decision<S> highestScored(Collection<Decision<S>> decisions) {
        return bestScored(decisions, true);
    }
    
    /** Return the Decision with the lowest score. */
    public static <S extends Position<S>> Decision<S> lowestScored(Collection<Decision<S>> decisions) {
        return bestScored(decisions, false);
    }
    
}
