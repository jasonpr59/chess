package main.java.player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An indication of which Move is best to make.
 * In the context of adversarial search, the adversaries
 * take turns changing the game's Position via Move.
 * @param <P> The type of Position this decision is made for.
 */
public class Decision<P extends Position<P>> {
    private final List<Move<P>> variation;
    private final Score score;

    /**
     * Create a decision with a foreseen variation and resulting score.
     * @param variation This move, and the optimal set of moves that is
     *      predicted to be made as a response.
     * @param score The score of the final position reached by the
     *      continuation, as predicted by some Heuristic.
     */
    public Decision(List<Move<P>> variation, Score score) {
        this.variation = Collections.unmodifiableList(variation);
        this.score = score;
    }

    /**
     * Get the list of Moves this Decision expects to make.
     * These Moves lead toward a final state whose score
     * is this Decision's score.
     */
    public List<Move<P>> getVariation() {
        return variation;
    }

    /**
     * Get the first Move to make.
     * This Decision indicates that this Move is the best
     * one to make, given the expected continuation.
     */
    public Move<P> getFirstMove() {
        return variation.get(0);
    }

    /**
     * Return the expected score.
     *
     * The expected score is the score of the State that will
     * result from optimal play for the depth explored to generate
     * this Decision, according to some heuristic.
     */
    public Score getScore() {
        return score;
    }

    /**
     * Return the Decision with the best score.
     * The best score is the highest if highest is true.  Otherwise,
     * the best score is the lowest.
     */
    public static <P extends Position<P>> Decision<P> bestScored(Collection<Decision<P>> decisions, boolean highestBest) {
        assert decisions.size() > 0;

        boolean seenAny = false;
        Decision<P> best = null;

        for (Decision<P> d : decisions) {
            if (!seenAny || isNewBest(d, best, highestBest)) {
                best = d;
                seenAny = true;
            }
        }
        return best;
    }

    public static <P extends Position<P>> boolean isNewBest(Decision<P> contender,
                                                            Decision<P> oldBest,
                                                            boolean highestBest) {
        Score contenderScore = contender.getScore();
        Score oldBestScore = oldBest.getScore();
        if (highestBest) {
            return contenderScore.greaterThan(oldBestScore);
        } else {
            return contenderScore.lessThan(oldBestScore);
        }
    }


    /** Return the Decision with the highest score. */
    public static <P extends Position<P>> Decision<P> highestScored(Collection<Decision<P>> decisions) {
        return bestScored(decisions, true);
    }

    /** Return the Decision with the lowest score. */
    public static <P extends Position<P>> Decision<P> lowestScored(Collection<Decision<P>> decisions) {
        return bestScored(decisions, false);
    }

}
