package chess.exceptions;

/** Indicates that a ChessMove cannot legally be made from a ChessPosition. */
public class InvalidMoveException extends ChessException {

    private static final long serialVersionUID = 1L;

    public InvalidMoveException() {
        super();
    }

    public InvalidMoveException(String msg) {
        super(msg);
    }
}
