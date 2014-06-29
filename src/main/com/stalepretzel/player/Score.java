package com.stalepretzel.player;

/**
 * The score of a Position.
 * Positive scores mean the maximizing player is favored.
 * Negative scores mean the minimizing player is favored.
 */
public abstract class Score {
    public abstract float getValue();
    public abstract boolean greaterThan(Score s);
    public boolean lessThan(Score s) {
        return !greaterThan(s) && !equals(s);
    }
}
