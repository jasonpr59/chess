package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import player.Outcome;
import chess.piece.King;
import chess.piece.Piece;


/**
 * Abstract implementation of most of the ChessPosition interface.
 *
 * Subclasses of AbstractChessPosition must define their getters,
 * such as getPiece and getToMoveColor.
 */
public abstract class AbstractChessPosition implements ChessPosition {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (Square s : Square.ALL) {
            Piece piece = getPiece(s);
            result = prime * result + (piece == null ? 0 : piece.hashCode());
        }

        Square enPassantSquare = getEnPassantSquare();
        result = prime * result + (enPassantSquare == null ? 0 : enPassantSquare.hashCode());
        result = prime * result + getToMoveColor().hashCode();
        result = prime * result + getCastlingInfo().hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractChessPosition)) {
            return false;
        }

        AbstractChessPosition other = (AbstractChessPosition) obj;
        for (Square s : Square.ALL) {
            if (getPiece(s) == null) {
                // Ensure the other one is null, too.
                if (other.getPiece(s) != null) {
                    return false;
                }
            } else if (!getPiece(s).equals(other.getPiece(s))) {
                return false;
            }
        }
        if (getToMoveColor() != other.getToMoveColor()) {
            return false;
        }
        if (getEnPassantSquare() == null) {
            if (other.getEnPassantSquare() != null) {
                return false;
            }
        } else if (!(getEnPassantSquare().equals(other.getEnPassantSquare()))) {
            return false;
        }
        if (!(getCastlingInfo().equals(other.getCastlingInfo()))) {
            return false;
        }
        return true;
    }

    // TODO: Make sure the generics magic allows both PromotionMove and ChessMove.
    @Override
    public Collection<ChessMove> moves() {
        Collection<ChessMove> legalMoves = new ArrayList<ChessMove>();
        for (Square start : Square.ALL) {
            for (ChessMove saneMove : saneMoves(start)) {
                if (saneMove.isLegal(this)) {
                    legalMoves.add(saneMove);
                }
            }
        }
        return legalMoves;
    }

    @Override
    public Outcome outcome() {
        if (checked(getToMoveColor())) {
            return Outcome.LOSS;
        } else {
            return Outcome.DRAW;
        }
    }

    @Override
    public boolean shouldMaximize() {
        return (getToMoveColor() == Piece.Color.WHITE);
    }

    /**
     * Return whether the given square is currently under attack.
     * A square is under attack if a piece of the toMoveColor is
     * attacking it.
     */
    private boolean isAttackable(Square target, Piece.Color attackerColor) {
        // We'd like to just return true if there's any piece of the
        // attackerColor that has a sane move to the target Square.
        // But, moves are only sane if the toMoveColor is equal to the
        // moving pieces color.
        // So, we create a trial position, which is identical to this position
        // EXCEPT that the toMoveColor is set equal to the attackerColor.
        ChessPosition trialPosition;
        if (getToMoveColor() == attackerColor) {
            // Already the correct toMoveColor.
            trialPosition = this;
        } else {
            // We need to switch the toMoveColor.
            trialPosition = new ChessPositionBuilder(this).setToMoveColor(getToMoveColor().opposite()).build();
        }

        // Now we just see if any piece of the attackerColor has a sane
        // move to the target Square.
        for (Square attackerSquare : Square.ALL) {
            Piece attacker = getPiece(attackerSquare);
            if (attacker == null || attacker.getColor() != attackerColor) {
                // No attacker on this square.
                continue;
            }
            if (new NormalChessMove(attackerSquare, target).isSane(trialPosition)) {
                return true;
            }
        }
        // Nobody attacks the target.
        return false;
    }

    /** Return the square that the king of some color occupies. */
    private Square kingSquare(Piece.Color kingColor) {
        // TODO: Make this more efficient by "caching" the king's position
        // as an attribute of board.
        Piece king = new King(kingColor);

        for (Square possibleKingSquare : Square.ALL){
            if (king.equals(getPiece(possibleKingSquare))) {
                return possibleKingSquare;
            }
        }
        // Didn't return anything!
        throw new RuntimeException("There is no king of color " + kingColor + " on the board!");
    }

    @Override
    public boolean checked(Piece.Color kingColor) {
        Square kingSquare = kingSquare(kingColor);
        return isAttackable(kingSquare, kingColor.opposite());
    }

    /** Get the set of sane moves available to the piece on a square. */
    private Iterable<ChessMove> saneMoves(Square start) {
        Piece movingPiece = getPiece(start);
        if (movingPiece == null) {
            // No piece at that square = no available moves!
            return new HashSet<ChessMove>();
        }
        // Let the piece tell us its sane moves in this position.
        return movingPiece.saneMoves(start, this);
    }

    @Override
    public boolean anyOccupied(Iterable<Square>squares) {
        for (Square square : squares) {
            if (getPiece(square) != null){
                return true;
            }
        }
        return false;
    }

}
