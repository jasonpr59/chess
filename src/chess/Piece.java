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
        BLACK;

        public PieceColor opposite() {
            switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                throw new RuntimeException("Unexpected PieceColor " + this);
            }
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
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        Piece that = (Piece) obj;
        return type == that.getType() && color == that.getPieceColor();
    }
    
    @Override
    public int hashCode() {
        return 17 * type.hashCode() + color.hashCode();
    }
    
    @Override
    public String toString() {
        return "Piece of type " + type + " and color "+ color;
    }
}
