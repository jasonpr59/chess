package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Minimax<P extends Position<P>> implements Decider<P>{

    private final Heuristic<P> heuristic;

    public Minimax(Heuristic<P> heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public Decision<P> bestDecision(P position, int depth) {
        int pliesFromRoot = 0;
        return minimax(position, pliesFromRoot, depth);
    }

    private Decision<P> minimax(P position, int pliesFromRoot, int maxPlies) {
        List<Move<P>> nextTransitions = new ArrayList<Move<P>>();
        if (pliesFromRoot > maxPlies) {
            throw new IllegalArgumentException("pliesFromRoot cannot be greater than maxPlies.");
        } else if (pliesFromRoot == maxPlies) {
            return new Decision<P>(nextTransitions, heuristic.value(position));
        } else {
            // Get all possible decisions.
            List<Decision<P>> possibleDecisions = new ArrayList<Decision<P>>();
            P possibleResult;
            List<Move<P>> transitions = new ArrayList<Move<P>>(position.moves());
            if (transitions.size() == 0) {
                TerminalScore mate;
                Outcome outcome = position.outcome();
                switch (outcome) {
                case WIN:
                    mate = TerminalScore.wins(position.toMove(), pliesFromRoot);
                    break;
                case DRAW:
                    mate = TerminalScore.draw(pliesFromRoot);
                    break;
                case LOSS:
                    mate = TerminalScore.loses(position.toMove(), pliesFromRoot);
                    break;
                default:
                    throw new RuntimeException("Illegal Outcome " + outcome);
                }
                return new Decision<P>(new ArrayList<Move<P>>(), mate);
            }

            Collections.shuffle(possibleDecisions);
            for (Decision<P> decision : possibleDecisions) {
                possibleResult = decision.getFirstMove().result(position);
                Decision<P> nextDecision = minimax(possibleResult, pliesFromRoot, maxPlies);
                nextTransitions = new ArrayList<Move<P>>();
                nextTransitions.add(decision.getFirstMove());
                nextTransitions.addAll(nextDecision.getVariation());
                possibleDecisions.add(new Decision<P>(nextTransitions, nextDecision.getScore()));
            }

            // Choose the possible decision that give the optimal score.
            Decision<P> bestDecision;
            if (position.toMove() == Player.MAXIMIZER) {
                bestDecision = Decision.highestScored(possibleDecisions);
            } else {
                bestDecision = Decision.lowestScored(possibleDecisions);
            }
            return bestDecision;
        }
    }
}
