package player;

// TODO: Maybe turn this into an inner class of Position?
/** A transition from one Position to another. */
public interface Transition<P extends Position<P>> {
    public P result(P position);
}
