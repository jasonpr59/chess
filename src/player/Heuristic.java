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
    public EstimatedScore value(P position);

    /**
     * Return the score of this terminal Position.
     * Requires that the Position is terminal, that is, that there are no
     * Moves from this Position.
     *
     * Sometimes the value of a Position is strongly affected by whether
     * the position is terminal.  For example, in a chess position, white
     * could have two extra queens, but still be checkmated!  A Heuristic
     * could choose not to check whether a Position is terminal, because
     * Move-generation is often expensive.  But, a Decider would commonly
     * enumerate moves from Positions.  So, a Decider could detect
     * that a Position is terminal, and request a terminalValue, if
     * it wanted to.
     *
     */
    public TerminalScore terminalValue(P position);
}
