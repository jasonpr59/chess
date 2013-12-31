package chess;

import java.util.ArrayList;
import java.util.Collection;

import chess.piece.Pawn;
import chess.piece.Piece;

/**
 * A normal (non-promoting, non-castling) move from one Square to another Square.
 *  This class is immutable.
 */
public class NormalChessMove implements ChessMove{

    private final Square start;
    private final Square end;
    // We'll cache the delta from start to end, since Move is immutable.
    private final Delta delta;

    /** Construct a Move from one Square to another Square. */
    public NormalChessMove(Square start, Square end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("A move cannot start and end on the same square.");
        }

        this.start = start;
        this.end = end;
        this.delta = new Delta(start, end);
    }

    /** Construct a NormalChessMove from one square to another square. */
    public NormalChessMove(String start, String end) {
        this(Square.algebraic(start), Square.algebraic(end));
    }

    /** Construct a NormalMove from starting at a Square and moving by a Delta. */
    public NormalChessMove(Square start, Delta delta) {
        this(start, start.plus(delta));
    }

    /** Construct a copy of another NormalChessMove. */
    public NormalChessMove(NormalChessMove that) {
        this(that.getStart(), that.getEnd());
    }

    @Override
    public Square getStart() {
        return start;
    }

    @Override
    public Square getEnd() {
        return end;
    }

    @Override
    public Delta getDelta() {
        return delta;
    }

    @Override
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

    @Override
    public Square enPassantSquare(ChessPosition board) {
        Piece movingPiece = board.getPiece(start);
        if (!(movingPiece instanceof Pawn)) {
            return null;
        }

        if (Math.abs(delta.getDeltaRank()) == 2) {
            return Square.mean(start, end);
        } else {
            return null;
        }
    }

    @Override
    public boolean isSane(ChessPosition board) {
        Piece movingPiece = board.getPiece(start);
        if (movingPiece == null) {
            return false;
        }
        return movingPiece.isSane(this, board);
    }

    @Override
    public boolean isLegal(ChessPosition board) {
        // All legal Moves are sane.
        if (!isSane(board)) {
            return false;
        }
        ChessPosition resultBoard = result(board);
        return !resultBoard.checked(board.getToMoveColor());
    }

    @Override
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
        assert delta.isDiagonal() || delta.isBasic();

        Delta unitStep = delta.unitized();
        Collection<Square> squares = new ArrayList<Square>();
        Square currentSquare = start.plus(unitStep);
        while (!currentSquare.equals(end)) {
            squares.add(currentSquare);
            currentSquare = currentSquare.plus(unitStep);
        }
        return squares;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        NormalChessMove that = (NormalChessMove) obj;
        return start.equals(that.getStart()) && end.equals(that.getEnd());
    }

    @Override
    public int hashCode() {
        return 17 * start.hashCode() + end.hashCode();
    }

    @Override
    public String toString(){
        return "NormalChessMove(" + start + ", " + end + ")";
    }

    /** Return whether move makes a pawn do a capture on a Board. */
    private boolean isPawnCapture(ChessPosition position) {
        Piece movingPiece = movingPiece(position);
        if (!(movingPiece instanceof Pawn)) {
            // Not a pawn.
            return false;
        }
        return delta.getDeltaFile() != 0;
    }

    @Override
    public boolean startsOrEndsAt(Square square) {
        return start.equals(square) || end.equals(square);
    }

    @Override
    public String serialized() {
        return "" + start.getFile() + start.getRank() + end.getFile() + end.getRank();
    }

    // TODO: Move this elsewhere.  Or, better, delete
    // it, and abandon this weird serialization scheme.
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
        return new NormalChessMove(Square.squareAt(startFile, startRank),
                Square.squareAt(endFile, endRank));
    }

    @Override
    public ChessPosition result(ChessPosition position) {
        Square start = getStart();
        Square end = getEnd();
        Piece movingPiece = movingPiece(position);

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

    /** Get the piece that moves when this ChessMove is made on a ChessPosition. */
    private Piece movingPiece(ChessPosition position) {
        return position.getPiece(getStart());
    }
}
