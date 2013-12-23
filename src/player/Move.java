package player;

/** A transition from one Position to another. */
public interface Move<P extends Position<P>> {
    public P result(P position);
}
