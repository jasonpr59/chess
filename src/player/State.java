package player;

import java.util.Collection;

public interface State<S extends State<S>> {
    public Collection<Transition<S>> transitions();
    public S result(Transition<S> transition);
}
