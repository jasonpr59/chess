package player;

public interface Heuristic<S> {
    // TODO: Figure out a legal alternative to making this a static method.    
    public float value(S state);
}
