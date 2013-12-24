package player;

/**
 * A heuristic function for some type of Position.
 * 
 * Heuristic functions, a.k.a. static evaluation functions,
 * are used to estimate who is winning a game based on a
 * single board position.
 * 
 * A simple chess heuristic function might just add up the
 * value of white pieces in a position, and subtract the
 * value of the black pieces.  A more nuanced heuristic might
 * consider features like king safety, central dominance, pawn
 * structure, etc.
 * 
 * @param <P> The type of positions this Heuristic will evaluate.
 */
public interface Heuristic<P extends Position<P>> {
    // TODO: Figure out a legal alternative to making this a static method.
    /** Evaluate the position according to this Heuristic. */
    public float value(P position);
}
