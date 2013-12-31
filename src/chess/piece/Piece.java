package chess.piece;

/**
 * A chess piece.
 * This class is immutable.
 */
public abstract class Piece {
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

    private final Color color;

    /** Construct a new Piece with some type and color. */
    public Piece(Color color){
        this.color = color;
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
        return color == that.getColor();
    }

    @Override
    public int hashCode() {
        return color.hashCode();
    }
}
