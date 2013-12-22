package player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An indication of which Transition is best to make.
 * In the context of adversarial search, the adversaries
 * take turns changing the game's State via Transitions.
 * @param <T> The type of Transition that changes the game
 *      from one State to another State.
 */
public class Decision<T> {
    private final List<T> transitionList;
    private final float score;
    
    public Decision(List<T> transitionList, float score) {
        this.transitionList = Collections.unmodifiableList(transitionList);
        this.score = score;
    }
    
    /**
     * Get the list of Transitions this Decision expects to make.
     * These Transitions lead toward a final state whose score
     * is this Decision's score.
     */
    public List<T> getList() {
        return transitionList;
    }
    
    /**
     * Get the first Transition to make.
     * This Decision indicates that this Transition is the best
     * one to make, given the expected continuation.
     */
    public T getFirst() {
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
    public static <T> Decision<T> bestScored(Collection<Decision<T>> decisions, boolean highestBest) {
        assert decisions.size() > 0;
        
        final float MULTIPLIER = highestBest ? +1.0f : -1.0f;
        
        boolean seenAny = false;
        // Optimal post-multiplication score.
        float optimalScore = 0.0f;
        Decision<T> best = null;

        float currentScore; 
        for (Decision<T> d : decisions) {
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
    public static <T> Decision<T> highestScored(Collection<Decision<T>> decisions) {
        return bestScored(decisions, true);
    }
    
    /** Return the Decision with the lowest score. */
    public static <T> Decision<T> lowestScored(Collection<Decision<T>> decisions) {
        return bestScored(decisions, false);
    }
    
}
