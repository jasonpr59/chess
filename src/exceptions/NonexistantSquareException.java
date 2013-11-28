package exceptions;

public class NonexistantSquareException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NonexistantSquareException(String message) {
        super(message);
    }
}
