package exceptions;

public class CheckException extends InvalidMoveException {

    public CheckException() {
        super();
    }

    public CheckException(String msg) {
        super(msg);
    }

}
