package player;

import java.util.Collection;

/**
 * A position in some game.
 * For example, this could be a chess position. 
 *
 * A Position captures the *entire* current state of
 * the game.  If there's any information that will
 * be required to determine which moves are legal,
 * or who wins, now or after additional moves are made,
 * then it must be contained in the Position.
 * 
 * Implementing classes must be immutable.
 *
 * @param <P> The type of Position that this is.
 *      TODO: Figure out something useful to say about
 *      this recursive type parameter.
 */
public interface Position<P extends Position<P>> {
    /** Return the set of legal Moves at this position. */ 
    public Collection<? extends Move<P>> moves();
    /**
     * The outcome of the game that ends in this position.
     * Requires that there are no legal Moves out of
     * this Position. 
     */
    public Outcome outcome();

    // TODO: Return a "current player id", instead,
    // so that there's no "unspoken agreement" between
    // this interface and Heuristic about when to maximize
    // or minimize.
    /**
     * Whether the player whose move it is should maximize
     * the board position (as opposed to minimizing it).
     */
    public boolean shouldMaximize();
}
