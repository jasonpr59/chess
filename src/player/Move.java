package player;

/** A transition from one Position to another. */
public interface Move<P extends Position<P>> {
    /** Get the Position resulting form making this Move from some Position. */
    public P result(P position);
}
