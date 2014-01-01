package chess;

import chess.CastlingInfo.Side;
import chess.piece.Piece;

public class CastlingMove implements ChessMove {
    @Override
    public int hashCode() {
        return baseMove.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CastlingMove other = (CastlingMove) obj;
        return baseMove.equals(other.baseMove);
    }

    private static final Delta KINGSIDE_DELTA = new Delta(2, 0);
    private static final Delta QUEENSIDE_DELTA = new Delta(-2, 0);

    private final NormalChessMove baseMove;

    public CastlingMove(NormalChessMove chessMove) {
        baseMove = chessMove;
    }

    /** Construct a Move from one Square to another Square. */
    public CastlingMove(Square start, Square end) {
        baseMove = new NormalChessMove(start, end);
    }

    /** Construct a Move from starting at a Square and moving by a Delta. */
    public CastlingMove(Square start, Delta delta) {
        baseMove = new NormalChessMove(start, delta);
    }

    @Override
    public Square capturedSquare(ChessPosition position) {
        // Castling never captures.
        return null;
    }

    @Override
    public Square enPassantSquare(ChessPosition position) {
        // Castling never creates an en passant Square.
        return null;
    }

    @Override
    public boolean isSane(ChessPosition position) {
        Piece movingPiece = position.getPiece(getStart());
        if (movingPiece == null) {
            return false;
        }
        return movingPiece.isSane(this, position);
    }

    private Piece.Color getColor(ChessPosition position) {
        return position.getPiece(baseMove.getStart()).getColor();
    }

    private CastlingInfo.Side getSide() {
        Delta delta = new Delta(baseMove);
        if (delta.equals(KINGSIDE_DELTA)) {
            return CastlingInfo.Side.KINGSIDE;
        } else if (delta.equals(QUEENSIDE_DELTA)) {
            return CastlingInfo.Side.QUEENSIDE;
        } else {
            throw new IllegalStateException("This CastlingMove has an impossible Delta.");
        }
    }

    /** Get the starting Square of the Rook involved in castling. */
    public static Square getRookStart(Piece.Color color, CastlingInfo.Side side) {
        String rank;
        String file;
        if (color == Piece.Color.WHITE) {
            rank = "1";
        } else {
            rank = "8";
        }
        if (side == Side.KINGSIDE) {
            file = "h";
        } else {
            file = "a";
        }
        return Square.algebraic(file + rank);
    }

    @Override
    public boolean isLegal(ChessPosition position){
        // All legal moves are sane.
        if (!isSane(position)) {
            return false;
        }
        // Ensure that king was not checked before moving.
        if (position.checked(position.getToMoveColor())) {
            return false;
        }

        Square start = baseMove.getStart();

        // Ensure that king did not move through check.
        // Do this by making the king move to that square, and seeing whether it is checked.
        Square transitSquare = start.plus(new Delta(baseMove).unitized());
        NormalChessMove loneKingMove = new NormalChessMove(start, transitSquare);
        if (!loneKingMove.isLegal(position)) {
            return false;
        }

        ChessPosition resultBoard = result(position);
        return !resultBoard.checked(position.getToMoveColor());
    }

    @Override
    public ChessPosition result(ChessPosition position) {
        // Move the king, normally.
        ChessPosition partlyMoved = baseMove.result(position);

        // Move the rook.
        // TODO: Do this without creating a second builder for the rook-move
        // step.  (The first one was in the super.result step.)
        ChessPositionBuilder builder = new ChessPositionBuilder(partlyMoved);

        Square rookStart = getRookStart(getColor(position), getSide());
        // Rook ends between king's start and king's end.
        Square rookEnd = Square.mean(baseMove.getStart(), baseMove.getEnd());
        Piece rook = partlyMoved.getPiece(rookStart);
        builder.placePiece(null, rookStart);
        builder.placePiece(rook, rookEnd);

        return builder.build();
    }

    @Override
    public Square getStart() {
        return baseMove.getStart();
    }

    @Override
    public Square getEnd() {
        return baseMove.getEnd();
    }

    @Override
    public Iterable<Square> passedThrough() {
        // There's a fair amount of duplicated code
        // between here and isSane.  I'm being lax because
        // I'll be seriously remodeling this class soon.
        // TODO: Remove this comment post-remodeling.

        Square kingStart = baseMove.getStart();

        Delta delta = new Delta(baseMove);
        CastlingInfo.Side side;
        if (delta.equals(KINGSIDE_DELTA)) {
            side = CastlingInfo.Side.KINGSIDE;
        } else if (delta.equals(QUEENSIDE_DELTA)) {
            side = CastlingInfo.Side.QUEENSIDE;
        } else {
            throw new IllegalStateException();
        }

        Piece.Color color;
        if (kingStart.getRank() == 1) {
            color = Piece.Color.WHITE;
        } else if (kingStart.getRank() == 8) {
            color = Piece.Color.BLACK;
        } else {
            throw new IllegalStateException();
        }
        Square rookStart = getRookStart(color, side);

        return Square.between(kingStart, rookStart);
    }

}
