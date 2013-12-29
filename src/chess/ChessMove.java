package chess;

import java.util.ArrayList;
import java.util.Collection;

import player.Move;

/**
 * A move from one Square to another Square.
 *  This class is immutable.
 */
public class ChessMove implements Move<ChessPosition>{

    private final Square start;
    private final Square end;
    // We'll cache the delta from start to end, since Move is immutable.
    private final Delta delta;

    /** Construct a Move from one Square to another Square. */
    public ChessMove(Square start, Square end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("A move cannot start and end on the same square.");
        }

        this.start = start;
        this.end = end;
        this.delta = new Delta(start, end);
    }

    /** Construct a Move from one square to another square. */
    public ChessMove(String start, String end) {
        this(Square.algebraic(start), Square.algebraic(end));
    }

    /** Construct a Move from starting at a Square and moving by a Delta. */
    public ChessMove(Square start, Delta delta) {
        this(start, start.plus(delta));
    }

    /** Construct a copy of another ChessMove. */
    public ChessMove(ChessMove that) {
        this(that.getStart(), that.getEnd());
    }

    public Square getStart() {
        return start;
    }

    public Square getEnd() {
        return end;
    }

    public Delta getDelta() {
        return delta;
    }

    /**
     * Return the square that is occupied by the piece that's to be captured by this move.
     * Return null if no piece is to be captured.
     * Takes en passant into account.
     * @param board The board on which the move is to be made.
     * Requires that this move is sane.
     */
    public Square capturedSquare(ChessPosition board) {
        if (board.getPiece(end) != null) {
            // There's something in the landing square, so that's what's captured.
            return end;
        } else if (isPawnCapture(board)){
            return board.getEnPassantSquare();
        } else {
            // Nothing at destination, and not a pawn.
            return null;
        }
    }

    /**
     * Return the en passant Square produced by this move, or null if there isn't one.
     * @param board The board on which the move is to be made.
     */
    public Square enPassantSquare(ChessPosition board) {
        Piece movingPiece = board.getPiece(start);
        if (movingPiece == null || movingPiece.getType() != Piece.Type.PAWN) {
            return null;
        }

        if (Math.abs(delta.getDeltaRank()) == 2) {
            return Square.mean(start, end);
        } else {
            return null;
        }
    }

    /**
     * Return whether this move is sane on a particular board.
     *
     * A move is sane if it is legal, IGNORING the no-check criterion.
     * That is, assert that the move is legal, except that the king might
     * be checked, or might have castled through or out of check.
     */
    public boolean isSane(ChessPosition board) {
        if (!isLandable(board)) {
            return false;
        }

        Piece movingPiece = board.getPiece(start);

        if (movingPiece == null || movingPiece.getColor() != board.getToMoveColor()) {
            return false;
        }

        switch (movingPiece.getType()) {
            case PAWN:
                // We'll need to know which way is forward for this pawn, later on.
                boolean isWhite = movingPiece.getColor() == Piece.Color.WHITE;
                Delta forward = new Delta(0, isWhite ? 1: -1);
                Delta doubleForward = forward.scaled(2);

                if (delta.getDeltaFile() == 0) {
                    // Pawn push.
                    // Square one ahead must be unoccupied, whether single- or
                    // double-push.
                    // Note, isLandable is NOT enough... it must be UNOCCUPIED, not just landable.
                    if (board.getPiece(start.plus(forward)) != null) {
                        return false;
                    }

                    if (delta.equals(forward)) {
                        return true;
                    } else if (delta.equals(doubleForward)) {
                        return (start.isOnPawnHomeRank(movingPiece.getColor()) &&
                                board.getPiece(start.plus(doubleForward)) == null);
                    } else {
                        return false;
                    }
                } else if (Math.abs(delta.getDeltaFile()) == 1) {
                    // Capture.
                    if (delta.getDeltaRank() != forward.getDeltaRank()) {
                        return false;
                    }
                    Piece capturedPiece = board.getPiece(end);
                    boolean normalCapture = (capturedPiece != null &&
                                             capturedPiece.getColor() != movingPiece.getColor());
                    boolean epCapture = end.equals(board.getEnPassantSquare());
                    return normalCapture || epCapture;
                } else {
                    return false;
                }
            case KNIGHT:
                return Math.abs(delta.getDeltaRank() * delta.getDeltaFile()) == 2;
            case BISHOP:
                return isDiagonal() && isOpen(board);
            case ROOK:
                return isBasic() && isOpen(board);
            case QUEEN:
                return (isBasic() || isDiagonal()) && isOpen(board);
            case KING:
                // Not a castling move (this is ChessMove.isSane, not CastlingMove.isSane)
                return (Math.abs(delta.getDeltaRank()) <= 1 && Math.abs(delta.getDeltaFile()) <= 1);
            default:
                throw new RuntimeException("The piece type was not matched in the switch statement.");
        }
    }

    /** Return whether a move is legal on a given board. */
    public boolean isLegal(ChessPosition board) {
     // All legal Moves are sane.
     if (!isSane(board)) {
         return false;
     }
     ChessPosition resultBoard = result(board);
     return !resultBoard.checked(board.getToMoveColor());
    }

    /**
     * Return whether a move is open on a given board.
     * A move is open if the Squares between its start and end
     * are all unoccupied.
     *
     * Requires that the move is basic or diagonal: for other moves,
     * there is no definition of Squares "between" the start and end.
     */
    public boolean isOpen(ChessPosition board) {
        for (Square s : between()) {
            if (board.getPiece(s) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the Squares between the start and end.
     * Requires that the move is basic or diagonal: for other moves,
     * there is no definition of Squares "between" the start and end.
     */
    private Collection<Square> between() {
        assert isDiagonal() || isBasic();

        Delta unitStep = delta.unitized();
        Collection<Square> squares = new ArrayList<Square>();
        Square currentSquare = start.plus(unitStep);
        while (!currentSquare.equals(end)) {
            squares.add(currentSquare);
            currentSquare = currentSquare.plus(unitStep);
        }
        return squares;
    }

    /**
     * Return whether the Piece at the start could land on the end Square.
     * A Piece could land on a Square iff that Square is unoccupied or it is
     * occupied by a piece of the opposite color.
     */
    private boolean isLandable(ChessPosition board) {
        Piece movingPiece = board.getPiece(start);
        Piece capturedPiece = board.getPiece(end);
        return (capturedPiece == null ||
                capturedPiece.getColor() != movingPiece.getColor());
    }

    private boolean isDiagonal() {
        return Math.abs(delta.getDeltaRank()) == Math.abs(delta.getDeltaFile());
    }

    /** Return whether this is a rook-like move.
     *  That is, return true iff this move is entirely along a rank
     *  or a file.
     *  Such moves are "basic" because the rank and file directions form
     *  a nice basis for all moves on the board.
     */
    private boolean isBasic() {
        return (delta.getDeltaRank() == 0) ^ (delta.getDeltaFile() == 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        ChessMove that = (ChessMove) obj;
        return start.equals(that.getStart()) && end.equals(that.getEnd());
    }

    @Override
    public int hashCode() {
        return 17 * start.hashCode() + end.hashCode();
    }

    @Override
    public String toString(){
        return "Move(" + start + ", " + end + ")";
    }

    /** Return whether move makes a pawn do a capture on a Board. */
    private boolean isPawnCapture(ChessPosition board) {
        Piece movingPiece = board.getPiece(start);
        if (movingPiece == null || movingPiece.getType() != Piece.Type.PAWN) {
            // Not a pawn.
            return false;
        }
        return delta.getDeltaFile() != 0;
    }

    /** Return whether this Move starts or ends at some Square. */
    public boolean startsOrEndsAt(Square square) {
        return start.equals(square) || end.equals(square);
    }

    /** Serialize this move as a 4-character String. */
    public String serialized() {
        return "" + start.getFile() + start.getRank() + end.getFile() + end.getRank();
    }

    /** Deserialize this move from a 4-character String. */
    public static ChessMove deserialized(String s) {
        s = s.trim();

        // This method is responsible for delegating to other
        // deserializers for special moves.
        if (s.length() == 5) {
            if (s.charAt(4) == 'C') {
                return CastlingMove.deserialized(s);
            } else {
                return PromotionMove.deserialized(s);
            }
        }

        // Once we've gotten here, we're in the "normal move
        // deserialization" part.
        assert s.length() == 4;
        int startFile = Integer.parseInt(s.substring(0,1));
        int startRank = Integer.parseInt(s.substring(1,2));
        int endFile = Integer.parseInt(s.substring(2,3));
        int endRank = Integer.parseInt(s.substring(3,4));
        return new ChessMove(Square.squareAt(startFile, startRank),
                             Square.squareAt(endFile, endRank));

    }

    @Override
    public ChessPosition result(ChessPosition position) {
            Square start = getStart();
            Square end = getEnd();
            Piece movingPiece = position.movingPiece(this);

            // Make an unfrozen copy that we can modify to effect the move.
            ChessPositionBuilder builder = new ChessPositionBuilder(position);

            // The captured square might not be in the end square (in the case of en passant).
            Square capturedSquare = capturedSquare(position);
            if (capturedSquare != null) {
                // Remove the captured piece.
                builder.placePiece(null, capturedSquare);
            }

            // Remove the piece from its starting position...
            builder.placePiece(null, start);
            // ... and put it in its final position.
            builder.placePiece(movingPiece, end);

            // Update extra board info.
            builder.setEnPassantSquare(enPassantSquare(position));
            builder.flipToMoveColor();
            // Keep track of whether castling will be allowable
            // in future moves.
            builder.updateCastlingInfo(this);

            return builder.build();
    }
}
