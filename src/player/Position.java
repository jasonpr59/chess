package player;

import java.util.Collection;

public interface Position<P extends Position<P>> {
    public Collection<? extends Move<P>> transitions();
    /**
     * The outcome of the game that ends in this position.
     * Requires that there are no legal Transitions out of
     * this Position. 
     */
    public Outcome outcome();
    public boolean shouldMaximize();
}
