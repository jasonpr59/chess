package player;

public class TerminalScore extends Score {

    private final Player winner;
    private final int pliesUntilWin;

    public TerminalScore(Player winner, int pliesUntilWin) {
        this.winner = winner;
        this.pliesUntilWin = pliesUntilWin;
    }

    @Override
    public float getValue() {
        return (winner == Player.MAXIMIZER) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
    }

    public int getPliesUntilWin() {
        return pliesUntilWin;
    }

    @Override
    public boolean greaterThan(Score s) {
        if (getValue() > s.getValue()) {
            return true;
        } else if (getClass() == s.getClass()) {
            TerminalScore that = (TerminalScore) s;
            if (winner == Player.MAXIMIZER) {
                // "Maximizer wins soon" has a higher score than "maximizer
                // wins in a while", because it is better for the maximizer.
                return pliesUntilWin < that.getPliesUntilWin();
            } else {
                // "Minimizer wins in a while" has a higher score than "minimizer
                // wins soon", because it is better for the maximizer.
                return pliesUntilWin > that.getPliesUntilWin();
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pliesUntilWin;
        result = prime * result + ((winner == null) ? 0 : winner.hashCode());
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
        if (pliesUntilWin != other.pliesUntilWin) {
            return false;
        }
        if (winner != other.winner) {
            return false;
        }
        return true;
    }
}
