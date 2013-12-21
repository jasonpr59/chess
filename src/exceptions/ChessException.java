package exceptions;

public class ChessException extends Exception {

    private static final long serialVersionUID = 1L;

    public ChessException() {
        super();
    }
    
    public ChessException(String msg){
        super(msg);
    }

}
