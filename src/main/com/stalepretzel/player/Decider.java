package com.stalepretzel.player;

/**
 * A strategy for deciding the best Moves to make from Positions.
 *
 * For example, one Decider implementation might implement the
 * Minimax strategy.  Another might implement the AlphaBeta strategy.
 *
 * @param <P> The type of positions that will be analyzed.
 */
public interface Decider<P extends Position<P>> {
    /** Get the best Decision from the Position.
     *
     * @param position The Position for which to make a decision.
     * @param depth The suggested depth to which to search, in plies.
     * @param heuristic The heuristic function with which to evaluate
     *      continuations.
     */
    public Decision<P> bestDecision(P position, int depth);
}
