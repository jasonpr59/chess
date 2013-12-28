package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import player.Move;
import player.Outcome;


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
            result = prime * result + getPiece(s).hashCode();
        }

        result = prime * result + getEnPassantSquare().hashCode();
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
            if (!getPiece(s).equals(other.getPiece(s))) {
                return false;
            }
        }
        if (getToMoveColor() != other.getToMoveColor()) {
            return false;
        }
        if (!(getEnPassantSquare().equals(other.getEnPassantSquare()))) {
            return false;
        }
        if (!(getCastlingInfo().equals(other.getCastlingInfo()))) {
            return false;
        }
        return true;
    }

    // TODO: Make sure the generics magic allows both PromotionMove and ChessMove.
    @Override
    public Collection<Move<ChessPosition>> moves() {
        Collection<Move<ChessPosition>> legalMoves = new ArrayList<Move<ChessPosition>>();
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

    @Override
    public boolean isEmpty(Square square) {
        return getPiece(square) == null;
    }

    @Override
    public Piece movingPiece(ChessMove move) {
        return getPiece(move.getStart());
    }

    @Override
    public boolean isAttackable(Square target) {
        Piece.Color attackerColor = getToMoveColor();
        for (Square attackerSquare : Square.ALL) {
            Piece attacker = getPiece(attackerSquare);
            if (attacker == null || attacker.getColor() != attackerColor) {
                // No attacker on this square.
                continue;
            }
            for (ChessMove m: saneMoves(attackerSquare)) {
                if (m.getEnd().equals(target)) {
                    return true;
                }
            }
        }
        // Nobody attacks the target.
        return false;
    }

    @Override
    public Square kingSquare(Piece.Color kingColor) {
        // TODO: Make this more efficient by "caching" the king's position
        // as an attribute of board.
        Piece king = new Piece(Piece.Type.KING, kingColor);

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
        ChessPosition trialPosition;
        if (getToMoveColor() == kingColor) {
            // Act as though it's the other side's turn, to see if they could attack the king.
            trialPosition = new ChessPositionBuilder(this).setToMoveColor(getToMoveColor().opposite()).build();
        } else {
            // It's the other color's turn, so see if they can attack this king.
            trialPosition = this;
        }
        return trialPosition.isAttackable(kingSquare);
    }

    @Override
    public Iterable<ChessMove> saneMoves(Square start) {
        Piece movingPiece = getPiece(start);

        if (movingPiece == null) {
            // No piece at that square = no available moves!
            return new HashSet<ChessMove>();
        }

        Collection<Square> candidateEnds;
        Collection<ChessMove> candidateMoves;

        switch (movingPiece.getType()) {
            case PAWN:
                boolean isWhite = movingPiece.getColor() == Piece.Color.WHITE;
                Delta fwd = new Delta(0, isWhite? 1 : -1);
                Collection<Delta> candidateDeltas = new ArrayList<Delta>();
                // There are at most four possible pawn moves (ignoring promotion choices).
                // Just try them all!
                candidateDeltas.add(fwd);
                candidateDeltas.add(fwd.scaled(2));
                candidateDeltas.add(Delta.sum(fwd, new Delta(1, 0)));
                candidateDeltas.add(Delta.sum(fwd,  new Delta(-1, 0)));

                candidateMoves = new ArrayList<ChessMove>();
                ChessMove candidateMove;
                for (Delta delta : candidateDeltas) {
                    try {
                        candidateMove = new ChessMove(start, delta);
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
                break;
            case KNIGHT:
                // Knight moves are specified by three binary parameters:
                // Direction along rank, direction along file, and
                // alignment of the L-shape's long leg.
                int[] rankDirs = {-1, 1};
                int[] fileDirs = {-1, 1};
                int[][] alignments = {{1, 2}, {2, 1}};

                int dRank;
                int dFile;
                Delta delta;
                candidateMoves = new ArrayList<ChessMove>();
                for (int rankDir : rankDirs) {
                    for (int fileDir : fileDirs) {
                        for (int[] alignment : alignments) {
                            dRank = rankDir * alignment[0];
                            dFile = fileDir * alignment[1];
                            delta = new Delta(dFile, dRank);
                            try {
                                candidateMoves.add(new ChessMove(start, delta));
                            } catch (ArrayIndexOutOfBoundsException e) {
                                // Swallow it. (See explanation in PAWN case).
                            }
                        }
                    }
                }
                break;
            case BISHOP:
                candidateEnds = start.explore(Delta.DIAGONAL_DIRS);
                candidateMoves = start.distributeOverEnds(candidateEnds);
                break;
            case ROOK:
                candidateEnds = start.explore(Delta.BASIC_DIRS);
                candidateMoves = start.distributeOverEnds(candidateEnds);
                break;
            case QUEEN:
                candidateEnds = start.explore(Delta.QUEEN_DIRS);
                candidateMoves = start.distributeOverEnds(candidateEnds);
                break;
            case KING:
                candidateEnds = start.explore(Delta.QUEEN_DIRS, 1);
                candidateMoves = start.distributeOverEnds(candidateEnds);
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
                break;
            default:
                throw new RuntimeException("The piece type was not matched in the switch statement.");
        }
        return filterSane(candidateMoves);
    }

    @Override
    public Collection<ChessMove> filterSane(Collection<ChessMove> candidates) {
        Set<ChessMove> saneMoves = new HashSet<ChessMove>();
        for (ChessMove c : candidates) {
            if (c.isSane(this)) {
                saneMoves.add(c);
            }
        }
        return saneMoves;
    }
}
