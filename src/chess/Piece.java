package chess;

public class Piece {
    public enum PieceType{
        PAWN,
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING
    }
    
    public enum PieceColor{
        WHITE,
        BLACK
    }
    
    public static PieceColor getOppositeColor(PieceColor pieceColor){
        if (pieceColor == PieceColor.WHITE){
            return PieceColor.BLACK;
        } else {
            return PieceColor.WHITE;
        }
    }

    // type is final.  Promoting a pawn must involve destroying the pawn
    // and creating a new piece of the promoted type.
    private final PieceType type;
    private final PieceColor color;
    
    public Piece(PieceType type, PieceColor color){
        this.type = type;
        this.color = color;
    }
    
    public PieceType getType(){
        return this.type;
    }
    
    public PieceColor getPieceColor(){
        return this.color;
    }    
}
