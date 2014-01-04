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
    public TerminalScore terminalValue(P position){
        // Any game is either a win, a loss, or a tie if there are no
        // legal transitions left.
        Outcome result = position.outcome();
        switch (result) {
        case WIN:
            return TerminalScore.wins(position.toMove(), 0);
        case DRAW:
            // TODO: Allow TerminalScore to indicate a draw.
            return TerminalScore.draw(0);
        case LOSS:
            return TerminalScore.loses(position.toMove(), 0);
        default:
            throw new AssertionError("Result was not a valid value.");
        }
    }
}
