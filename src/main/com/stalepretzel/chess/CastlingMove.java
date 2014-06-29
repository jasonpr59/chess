package com.stalepretzel.chess;

import com.stalepretzel.chess.piece.Piece;

public class CastlingMove implements ChessMove {

    public enum Side { KINGSIDE, QUEENSIDE; }

    private static final Delta KINGSIDE_DELTA = new Delta(2, 0);
    private static final Delta QUEENSIDE_DELTA = new Delta(-2, 0);

    // Convenience squares, for deciding whether kings/rooks have moved.
    // These are the same as the ones defined in CastlingInfo.
    // TODO: Decide whether to factor out these common square definitions
    // into a helper class.
    private static final Square E1 = Square.squareAt(5, 1);
    private static final Square E8 = Square.squareAt(5, 8);

    private final Side side;
    private final Piece.Color color;

    public CastlingMove(Side side, Piece.Color color) {
        this.side = side;
        this.color = color;
    }

    public static CastlingMove fromNormalMove(NormalChessMove move) {
        int startRank = move.getStart().getRank();
        int startFile = move.getStart().getFile();
        int endRank = move.getEnd().getRank();
        int endFile = move.getEnd().getFile();

        if (startFile != 5) {
            throw new IllegalArgumentException("Moving piece must start on the a-file.");
        }

        if (startRank != endRank) {
            throw new IllegalArgumentException("Start and end ranks must be equal.");
        }

        Piece.Color color;
        if (startRank == 1) {
            color = Piece.Color.WHITE;
        } else if (startRank == 8) {
            color = Piece.Color.BLACK;
        } else {
            throw new IllegalArgumentException("Start rank must be 1 or 8.");
        }

        Side side;
        if (endFile == 3) {
            side = Side.QUEENSIDE;
        } else if (endFile == 7) {
            side = Side.KINGSIDE;
        } else {
            throw new IllegalArgumentException("End file must be 3 or 7.");
        }

        return new CastlingMove(side, color);
    }

    @Override
    public int hashCode() {
        return (side == Side.KINGSIDE ? 2 : 0) +
               (color == Piece.Color.WHITE ? 1 : 0);
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
        return (side == other.getSide() && color == other.getColor());
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

    public Piece.Color getColor() {
        return color;
    }

    public Side getSide() {
        return side;
    }

    private Delta getDelta () {
        return (side == Side.KINGSIDE ? KINGSIDE_DELTA : QUEENSIDE_DELTA);
    }

    /** Get the starting Square of the Rook involved in castling. */
    public Square getRookStart() {
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

    /** Get the ending Square of the Rook involved in castling. */
    public Square getRookEnd() {
        // The rook ends between the king's start and end squares.
        return Square.mean(getStart(), getEnd());
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
        NormalChessMove loneKingMove = new NormalChessMove(start, transitSquare);
        if (!loneKingMove.isLegal(position)) {
            return false;
        }

        ChessPosition resultBoard = result(position);
        return !resultBoard.checked(position.getToMoveColor());
    }

    @Override
    public ChessPosition result(ChessPosition position) {
        ChessPositionBuilder builder = new ChessPositionBuilder(position);

        // Move the king.
        Piece movingKing = position.getPiece(getStart());
        builder.vacate(getStart());
        builder.placePiece(movingKing, getEnd());

        // Move the rook.
        Square rookStart = getRookStart();
        Piece movingRook = position.getPiece(rookStart);
        builder.vacate(rookStart);
        builder.placePiece(movingRook, getRookEnd());

        // Clear en-passant square.
        builder.setEnPassantSquare(null);
        builder.flipToMoveColor();
        builder.updateCastlingInfo(this);

        return builder.build();
    }

    @Override
    public Square getStart() {
        if (color == Piece.Color.WHITE) {
            return E1;
        } else {
            return E8;
        }
    }

    @Override
    public Square getEnd() {
        int endRank = color == Piece.Color.WHITE ? 1 : 8;
        int endFile = side == Side.KINGSIDE ? 7 : 3;
        return Square.squareAt(endFile, endRank);
    }

    @Override
    public Iterable<Square> passedThrough() {
        Square kingStart = getStart();
        return Square.between(kingStart, getRookStart());
    }
}

