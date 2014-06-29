package com.stalepretzel.player;

public class TerminalScore extends Score {

    public static TerminalScore HIGHEST = TerminalScore.wins(Player.MAXIMIZER, 0);
    public static TerminalScore LOWEST = TerminalScore.loses(Player.MAXIMIZER, 0);

    private final Outcome outcomeForMaximizer;
    private final int pliesUntilEnd;

    private TerminalScore(Outcome outcomeForMaximizer, int pliesUntilEnd) {
        this.outcomeForMaximizer = outcomeForMaximizer;
        this.pliesUntilEnd = pliesUntilEnd;
    }

    public static TerminalScore wins(Player winner, int pliesUntilEnd) {
        Outcome outcomeForMaximizer;
        if (winner == Player.MAXIMIZER) {
            outcomeForMaximizer = Outcome.WIN;
        } else {
            outcomeForMaximizer = Outcome.LOSS;
        }
        return new TerminalScore(outcomeForMaximizer, pliesUntilEnd);
    }

    public static TerminalScore loses(Player loser, int pliesUntilEnd) {
        Outcome outcomeForMaximizer;
        if (loser == Player.MAXIMIZER) {
            outcomeForMaximizer = Outcome.LOSS;
        } else {
            outcomeForMaximizer = Outcome.WIN;
        }
        return new TerminalScore(outcomeForMaximizer, pliesUntilEnd);
    }

    public static TerminalScore draw(int pliesUntilEnd) {
        return new TerminalScore(Outcome.DRAW, pliesUntilEnd);
    }

    @Override
    public float getValue() {
        switch (outcomeForMaximizer) {
        case WIN:
            return Float.POSITIVE_INFINITY;
        case DRAW:
            return 0.0f;
        case LOSS:
            return Float.NEGATIVE_INFINITY;
        default:
            throw new RuntimeException("Unexpected outcome " + outcomeForMaximizer);
        }
    }

    public int getPliesUntilEnd() {
        return pliesUntilEnd;
    }

    @Override
    public boolean greaterThan(Score s) {
        if (getValue() > s.getValue()) {
            return true;
        } else if (getClass() == s.getClass()) {
            TerminalScore that = (TerminalScore) s;
            if (outcomeForMaximizer == Outcome.WIN) {
                // "Maximizer wins soon" has a higher score than "maximizer
                // wins in a while", because it is better for the maximizer.
                return pliesUntilEnd < that.getPliesUntilEnd();
            } else if (outcomeForMaximizer == Outcome.LOSS){
                // "Minimizer wins in a while" has a higher score than "minimizer
                // wins soon", because it is better for the maximizer.
                return pliesUntilEnd > that.getPliesUntilEnd();
            } else {
                // TODO: Figure out what we should actually return for draws.
                return pliesUntilEnd > that.getPliesUntilEnd();
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((outcomeForMaximizer == null) ? 0 : outcomeForMaximizer
                        .hashCode());
        result = prime * result + pliesUntilEnd;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TerminalScore other = (TerminalScore) obj;
        if (outcomeForMaximizer != other.outcomeForMaximizer) {
            return false;
        }
        if (pliesUntilEnd != other.pliesUntilEnd) {
            return false;
        }
        return true;
    }

}
