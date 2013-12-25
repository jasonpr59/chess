package exceptions;

/** Indicates that a String is not a legal ChesMove in algebraic notation. */
public class AlgebraicNotationException extends Exception {
    private static final long serialVersionUID = 1L;

    public AlgebraicNotationException() {
        super();
    }
    
    public AlgebraicNotationException(String msg) {
        super(msg);
    }
}
