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
    
    public boolean hasMoveAbility(Square start, Square end, Square enPassantSquare){
        int deltaFile = end.getFile() - start.getFile();
        int deltaRank = end.getRank() - start.getRank();
        
        if (type == PieceType.ROOK) {
            return (deltaRank == 0) ^ (deltaFile == 0);
        } else if (type == PieceType.BISHOP){
            return Math.abs(deltaRank) == Math.abs(deltaFile);
        } else if (type == PieceType.QUEEN){
            return ((deltaRank == 0) ^ (deltaFile == 0)) ||
                    (Math.abs(deltaRank) == Math.abs(deltaFile));
        } else if (type == PieceType.KNIGHT){
            // TODO: Implement
        } else if (type == PieceType.KING){
            // TODO: Implement
        } else if (type == PieceType.PAWN){
            //TODO: Implement
        } else {
            throw new IllegalArgumentException("Invalid piece type " + type);
        }
    }
    
}
