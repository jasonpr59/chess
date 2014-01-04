package player;

/**
 * A partial helper implementation of the Heuristic interface.
 *
 * Implementers must implement Heuristic.value.
 */
public abstract class AbstractHeuristic<P extends Position<P>> implements Heuristic<P> {

    /**
     * Return the score of this terminal Position.
     * Requires that the Position is terminal, that is, that there are no
     * Moves from this Position.
     */
    @Override
    public float terminalValue(P position){
        // Any game is either a win, a loss, or a tie if there are no
        // legal transitions left.

        Outcome result = position.outcome();

        float outcomeScore;
        switch (result) {
        case WIN:
            // Never the case: if you have no moves, you are
            // either checkmated or stalemated.
            throw new AssertionError("You never win if you have no moves!");
        case DRAW:
            // TODO: Decide whether to explicitly define a Draw Score.
            outcomeScore = 0.0f;
            break;
        case LOSS:
            // FIXME: This seems overly hacky.
            // This could probably be fixed by making *every* step
            // a "maximizing step," and just negating scores in each
            // recursive call.
            if (position.toMove() == Player.MAXIMIZER) {
                // The maximizing player lost.
                // TODO: Differentiate between "lose now" and "lose in x moves."
                outcomeScore = -10000.0f;
            } else {
                outcomeScore = 10000.0f;
            }
            break;
        default:
            throw new AssertionError("Result was not a valid value.");
        }
        return outcomeScore;
    }
}
