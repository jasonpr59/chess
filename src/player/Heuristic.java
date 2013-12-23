package player;

public interface Heuristic<P extends Position<P>> {
    // TODO: Figure out a legal alternative to making this a static method.    
    public float value(P position);
}
