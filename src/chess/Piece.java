package chess;

/**
 * A chess piece.
 * This class is immutable.
 */
public class Piece {
    /** The type of this piece: pawn, rook, king, etc. */
    public enum Type{
        PAWN,
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING
    }

    /** The color of this piece: white or black. */
    public enum Color{
        WHITE,
        BLACK;

        public Color opposite() {
            switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                throw new RuntimeException("Unexpected Color " + this);
            }
        }
    }

    // These fields are final (Piece is immutable).
    // So, promoting a pawn must involve destroying the pawn
    // and creating a new piece of the promoted type.
    private final Type type;
    private final Color color;

    /** Construct a new Piece with some type and color. */
    public Piece(Type type, Color color){
        this.type = type;
        this.color = color;
    }

    public Type getType(){
        return this.type;
    }

    public Color getColor(){
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
        return type == that.getType() && color == that.getColor();
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
