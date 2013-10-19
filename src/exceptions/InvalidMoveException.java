package exceptions;

public class InvalidMoveException extends ChessException {

    public InvalidMoveException() {
        super();
    }

    public InvalidMoveException(String msg) {
        super(msg);
    }
}
