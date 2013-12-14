package chess;

import java.util.ArrayList;
import java.util.Collection;

import javax.activity.InvalidActivityException;

import chess.Piece.PieceType;
import exceptions.InvalidMoveException;

public class Move {

    private final Square start;
    private final Square end;
    private final Delta delta;

    public Move(Square start, Square end) {
        this.start = start;
        this.end = end;

        // Acceptable to use "this" here: Delta only uses start and end,
        // which were already set.
        delta = new Delta(this);
        
        if (delta.getDeltaFile() == 0 && delta.getDeltaRank() == 0) {
            throw new IllegalArgumentException("A move cannot start and end on the same square.");
        }
    }
    
    public Move(Square start, Delta delta) {
        this(start, start.plus(delta));
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
     * Return whether this move is sane on a particular board.
     * 
     * A move is sane if it is legal, IGNORING the no-check criterion.
     * That is, assert that the move is legal, except that the king might
     * be checked, or might have castled through or out of check.
     */
    public boolean isSane(Board board) {
        if (!isLandable(board)) {
            return false;
        }
        
        Piece movingPiece = board.getPiece(start);
        
        if (movingPiece == null || movingPiece.getPieceColor() != board.getToMoveColor()) {
            return false;
        }
        
        switch (movingPiece.getType()) {
            case PAWN:
                // We'll need to know which way is forward for this pawn, later on.
                boolean isWhite = movingPiece.getPieceColor() == Piece.PieceColor.WHITE;
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
                        return board.getPiece(start.plus(doubleForward)) == null;
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
                                             capturedPiece.getPieceColor() != movingPiece.getPieceColor());
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
                // TODO(jasonpr): Handle castling!
                return (Math.abs(delta.getDeltaRank()) <= 1 && Math.abs(delta.getDeltaFile()) <= 1);
            default:
                throw new RuntimeException("The piece type was not matched in the switch statement.");
        }
    }
    
    public boolean isLegal(Board board) {
        Board resultBoard;
        try {
            resultBoard = board.moveResult(this);
        } catch (InvalidMoveException e) {
            return false;
        }
        return !resultBoard.checked(board.getToMoveColor());
    }
    
    private boolean isOpen(Board board) {
        assert isDiagonal() || isBasic();
        for (Square s : between()) {
            if (board.getPiece(s) != null) {
                return false;
            }
        }
        return true;
    }

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
    
    private boolean isLandable(Board board) {
        Piece movingPiece = board.getPiece(start);
        Piece capturedPiece = board.getPiece(end);
        return (capturedPiece == null ||
                capturedPiece.getPieceColor() != movingPiece.getPieceColor());
    }

    private boolean isDiagonal() {
        return Math.abs(delta.getDeltaRank()) == Math.abs(delta.getDeltaFile());
    }
        
    private boolean isBasic() {
        return (delta.getDeltaRank() == 0) ^ (delta.getDeltaFile() == 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Move that = (Move) obj;
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
    
    /**
     * Determine the square that is occupied by the piece that's to be captured by this move.
     * (Takes en passant into account.)
     * @param board The board on which the move is to be made.
     * @return The square that is occupied by the to-be-captured piece,
     *  or null if no piece is to be captured.
     *  @requires That this move is sane. 
     */
    public Square capturedSquare(Board board) {
        // TODO(jasonpr): Factor some common code out of here and isSane.
        Piece movingPiece = board.getPiece(start);
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
    
    public Square enPassantSquare(Board board) {
        Piece movingPiece = board.getPiece(start);
        if (movingPiece == null || movingPiece.getType() != Piece.PieceType.PAWN) {
            return null;
        }
        
        if (Math.abs(delta.getDeltaRank()) == 2) {
            return Square.mean(start, end);
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @return
     * @requires that this move is sane.
     */
    private boolean isPawnCapture(Board board) {
        Piece movingPiece = board.getPiece(start);
        if (movingPiece == null || movingPiece.getType() != Piece.PieceType.PAWN) {
            // Not a pawn.
            return false;
        }
        return delta.getDeltaFile() != 0;
    }
    
    public String serialized() {
        return "" + start.getFile() + start.getRank() + end.getFile() + end.getRank();
    }
    
    public static Move deserialized(String s) {
        s = s.trim();
        assert s.length() == 4;
        int startFile = Integer.parseInt(s.substring(0,1));
        int startRank = Integer.parseInt(s.substring(1,2));
        int endFile = Integer.parseInt(s.substring(2,3));
        int endRank = Integer.parseInt(s.substring(3,4));
        return new Move(Square.squareAt(startFile, startRank),
                        Square.squareAt(endFile, endRank));
        
    }
    
    
}
