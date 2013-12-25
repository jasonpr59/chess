package chess.exceptions;

/** Indicates that a square outside the 8x8 chessboard was requested. */
public class NonexistentSquareException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NonexistentSquareException() {
        super();
    }
    
    public NonexistentSquareException(String message) {
        super(message);
    }
}
