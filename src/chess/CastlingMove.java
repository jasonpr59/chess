package chess;

import chess.CastlingInfo.Side;

public class CastlingMove extends ChessMove {

    private static final Delta KINGSIDE_DELTA = new Delta(2, 0);
    private static final Delta QUEENSIDE_DELTA = new Delta(-2, 0);
    public CastlingMove(ChessMove chessMove) {
        super(chessMove);
    }

    /** Construct a Move from one Square to another Square. */
    public CastlingMove(Square start, Square end) {
        super(start, end);
    }

    /** Construct a Move from starting at a Square and moving by a Delta. */
    public CastlingMove(Square start, Delta delta) {
        super(start, delta);
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
        Square start = getStart();

        // Ensure the moving piece is a king.
        Piece movingPiece = position.getPiece(start);
        if (movingPiece.getType() != Piece.Type.KING) {
            return false;
        }

        // Ensure the start square is a king square.
        if (!start.equals(Square.algebraic("e1")) &&
            !start.equals(Square.algebraic("e8"))) {
            return false;
        }

        // Ensure the Delta is a castling delta.
        Delta delta = getDelta();
        CastlingInfo.Side side;
        if (delta.equals(KINGSIDE_DELTA)) {
            side = CastlingInfo.Side.KINGSIDE;
        } else if (delta.equals(QUEENSIDE_DELTA)) {
            side = CastlingInfo.Side.QUEENSIDE;
        } else {
            return false;
        }

        // Ensure the pieces are ready to castle.
        CastlingInfo castlingInfo = position.getCastlingInfo();
        // If the moving piece is a king in its OWN home square, then
        // this CastlingInfo check will ensure that he is ready to castle.
        // If the moving piece is a king in the OTHER king's home square, then
        // neither king is in his own home square, so neither king is ready to
        // castle, and this check will return false, correctly.
        if (!castlingInfo.castlePiecesReady(movingPiece.getColor(), side)) {
            return false;
        }

        // Finally, just ensure that there's space all the way between the king
        // and its rook.
        Square rookStart = getRookStart(movingPiece.getColor(), side);
        // There's space if the ChessMove from the king square to the rook square
        // is open.
        return new ChessMove(start, rookStart).isOpen(position);
    }

    private Piece.Color getColor(ChessPosition position) {
        return position.getPiece(getStart()).getColor();
    }

    private CastlingInfo.Side getSide() {
        Delta delta = getDelta();
        if (delta.equals(KINGSIDE_DELTA)) {
            return CastlingInfo.Side.KINGSIDE;
        } else if (delta.equals(QUEENSIDE_DELTA)) {
            return CastlingInfo.Side.QUEENSIDE;
        } else {
            throw new IllegalStateException("This CastlingMove has an impossible Delta.");
        }
    }

    private Square getRookStart(Piece.Color color, CastlingInfo.Side side) {
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

        Square start = getStart();

        // Ensure that king did not move through check.
        // Do this by making the king move to that square, and seeing whether it is checked.
        Square transitSquare = start.plus(getDelta().unitized());
        ChessMove loneKingMove = new ChessMove(start, transitSquare);
        if (!loneKingMove.isLegal(position)) {
            return false;
        }

        ChessPosition resultBoard = result(position);
        return !resultBoard.checked(position.getToMoveColor());
    }

    @Override
    public ChessPosition result(ChessPosition position) {
        // Move the king, normally.
        ChessPosition partlyMoved = super.result(position);

        // Move the rook.
        // TODO: Do this without creating a second builder for the rook-move
        // step.  (The first one was in the super.result step.)
        ChessPositionBuilder builder = new ChessPositionBuilder(partlyMoved);

        Square rookStart = getRookStart(getColor(position), getSide());
        // Rook ends between king's start and king's end.
        Square rookEnd = Square.mean(getStart(), getEnd());
        Piece rook = partlyMoved.getPiece(rookStart);
        builder.placePiece(null, rookStart);
        builder.placePiece(rook, rookEnd);

        return builder.build();
    }

    /** Deserialize this move from a 5-character String. */
    public static CastlingMove deserialized(String s) {
        assert s.length() == 5;
        assert s.charAt(4) == 'C';

        ChessMove basicMove = ChessMove.deserialized(s.substring(0, 4));
        return new CastlingMove(basicMove);
    }

}
