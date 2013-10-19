package exceptions;

public class PieceAbilityException extends InvalidMoveException{

    public PieceAbilityException() {
        super();
    }

    public PieceAbilityException(String msg){
        super(msg);
    }
}
