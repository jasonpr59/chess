package player;

import java.util.ArrayList;
// TODO: Determine whether this class should exist.
// The fact that it's an abstract class with no instance methods is
// one strike against it.
/**
 * A convenience parent class for Decider implementations.
 * Used for storing methods that might be useful for many Deciders. 
 *
 * AbstractDecider implements Decider, but does not implement its methods.
 * So, descendants of AbstractDecider must implement the methods defined
 * in Decider.
 *
 * @param <P> The type of Position for which decisions will be made.
 */
public abstract class AbstractDecider<P extends Position<P>> implements Decider<P>{
    // TODO: Decide whether this method belongs here.  It could belong in Heuristic...
    // or maybe some AbstractHeuristic.
    /**
     * Return the Decision that would be made by a terminal Position.
     * Requires that the Position is terminal, that is, that there are no
     * Moves from this Position.
     * @param position
     * @return
     */
    public static <P extends Position<P>> Decision<P> terminalDecision(P position){
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
            if (position.shouldMaximize()) {
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
        return new Decision<P>(new ArrayList<Move<P>>(), outcomeScore);
    }
}
