package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import player.Outcome;
import chess.piece.Bishop;
import chess.piece.King;
import chess.piece.Knight;
import chess.piece.Pawn;
import chess.piece.Piece;
import chess.piece.Queen;
import chess.piece.Rook;


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

        Collection<Square> candidateEnds;
        Collection<ChessMove> candidateMoves = new ArrayList<ChessMove>();

        if (movingPiece instanceof Pawn) {
            boolean isWhite = movingPiece.getColor() == Piece.Color.WHITE;
            Delta fwd = new Delta(0, isWhite? 1 : -1);
            Collection<Delta> candidateDeltas = new ArrayList<Delta>();
            // There are at most four possible pawn moves (ignoring promotion choices).
            // Just try them all!
            candidateDeltas.add(fwd);
            candidateDeltas.add(fwd.scaled(2));
            candidateDeltas.add(Delta.sum(fwd, new Delta(1, 0)));
            candidateDeltas.add(Delta.sum(fwd,  new Delta(-1, 0)));

            NormalChessMove candidateMove;
            for (Delta delta : candidateDeltas) {
                try {
                    candidateMove = new NormalChessMove(start, delta);
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
                int endRank = candidateMove.getEnd().getRank();
                if (endRank == 8 || endRank == 1) {
                    // It's a promotion!
                    candidateMoves.addAll(PromotionMove.allPromotions(candidateMove));
                } else {
                    // It's a non-promotion.
                    candidateMoves.add(candidateMove);
                }
            }
        } else if (movingPiece instanceof Knight) {
            // Knight moves are specified by three binary parameters:
            // Direction along rank, direction along file, and
            // alignment of the L-shape's long leg.
            int[] rankDirs = {-1, 1};
            int[] fileDirs = {-1, 1};
            int[][] alignments = {{1, 2}, {2, 1}};

            int dRank;
            int dFile;
            Delta delta;
            for (int rankDir : rankDirs) {
                for (int fileDir : fileDirs) {
                    for (int[] alignment : alignments) {
                        dRank = rankDir * alignment[0];
                        dFile = fileDir * alignment[1];
                        delta = new Delta(dFile, dRank);
                        try {
                            candidateMoves.add(new NormalChessMove(start, delta));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            // Swallow it. (See explanation in PAWN case).
                        }
                    }
                }
            }
        } else if (movingPiece instanceof Bishop) {
            candidateEnds = start.explore(Delta.DIAGONAL_DIRS);
            candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        } else if (movingPiece instanceof Rook) {
            candidateEnds = start.explore(Delta.BASIC_DIRS);
            candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        } else if (movingPiece instanceof Queen) {
            candidateEnds = start.explore(Delta.QUEEN_DIRS);
            candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
        } else if (movingPiece instanceof King) {
            candidateEnds = start.explore(Delta.QUEEN_DIRS, 1);
            candidateMoves.addAll(start.distributeOverEnds(candidateEnds));
            if (start.getFile() == 5) {
                // There's a decent chance that the king's in its home square,
                // and a zero chance that a two-square hop along a rank will
                // put us off the board.
                // TODO: Figure out how to add these directly to candidateMoves,
                // without making the generics gods sad.
                Collection<CastlingMove> castlingMoves = new HashSet<CastlingMove>();
                castlingMoves.add(new CastlingMove(start, new Delta(2, 0)));
                castlingMoves.add(new CastlingMove(start, new Delta(-2, 0)));
                candidateMoves.addAll(castlingMoves);
            }
        } else {
            throw new RuntimeException("The piece type was not matched in the switch statement.");
        }
        return filterSane(candidateMoves);
    }

    /** Return the subset of sane moves from a set of moves. */
    private Collection<ChessMove> filterSane(Collection<ChessMove> candidates) {
        Set<ChessMove> saneMoves = new HashSet<ChessMove>();
        for (ChessMove c : candidates) {
            if (c.isSane(this)) {
                saneMoves.add(c);
            }
        }
        return saneMoves;
    }
}
