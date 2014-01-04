package player;

/** A Score whose value is estimated, usually by some static evaluator like a Heuristic. */
public class EstimatedScore extends Score {

    private final float value;

    public EstimatedScore(float value) {
        this.value = value;
    }

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public boolean greaterThan(Score s) {
        return value > s.getValue();
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(value);
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
        EstimatedScore other = (EstimatedScore) obj;
        return Float.floatToIntBits(value) == Float.floatToIntBits(other.value);
    }


}
