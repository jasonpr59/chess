package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import chess.Piece.PieceColor;
import exceptions.InvalidMoveException;
import exceptions.NonexistantSquareException;
import exceptions.PieceAbilityException;

public class Board {

    // Once frozen is true, the board becomes immutable.
    private boolean frozen = false;
    
    
    // board[file - 1][rank - 1] = the piece with specified rank and file
    // For example, board[1][7] is the piece at b8.
    private final Piece[][] board;
    
    private Set<Piece> captured;
    
    private Square enPassantSquare;
    
    private PieceColor toMoveColor;
    
    private Board() {
        board = new Piece[8][8];

        // No captured pieces, yet.
        captured = new HashSet<Piece>();
        
        // No en passant square, yet.
        enPassantSquare = null;
    }
    
    /**
     * Construct a new board in the default chess starting position.
     */
    public static Board newGame() {
        Board b = new Board();
        
        // Set up the pawns
        for (int file = 1; file <= 8; file++){
            b.placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.WHITE),
                         Square.squareAt(file, 2));
            b.placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.BLACK),
                         Square.squareAt(file, 7));
        }
        
        // Set up the pieces
        b.placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE),
                    Square.squareAt(1, 1));
        b.placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK),
                    Square.squareAt(1, 8));
        
        b.placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE),
                    Square.squareAt(2, 1));
        b.placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK),
                    Square.squareAt(2, 8));
        
        b.placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE),
                    Square.squareAt(3, 1));
        b.placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK),
                    Square.squareAt(3, 8));
        
        b.placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.WHITE),
                    Square.squareAt(4, 1));
        b.placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.BLACK),
                    Square.squareAt(4, 8));
        
        b.placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.WHITE),
                    Square.squareAt(5, 1));
        b.placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.BLACK),
                    Square.squareAt(5, 8));
        
        b.placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE),
                    Square.squareAt(6, 1));
        b.placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK),
                    Square.squareAt(6, 8));
        
        b.placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE),
                    Square.squareAt(7, 1));
        b.placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK),
                    Square.squareAt(7, 8));
        
        b.placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE),
                    Square.squareAt(8, 1));
        b.placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK),
                    Square.squareAt(8, 8));
        
        b.setToMoveColor(Piece.PieceColor.WHITE);
        b.freeze();
        return b;
    }
    
    private Board unfrozenCopy() {
        Board b = new Board();
        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                Square sq = Square.squareAt(file, rank);
                b.placePiece(getPiece(sq), sq);
            }
        }
        // setCaptured makes a copy and discards the input set,
        // so we don't need to pass in a copy.
        b.setCaptured(captured);
        b.setEnPassantSquare(enPassantSquare);
        
        return b;
    }

    /**
     * Return the result of performing a move on this board.
     * @param move A Move object designating which move should be made.
     */
    public Board moveResult(Move move) throws InvalidMoveException{
        Square start = move.getStart();
        Square end = move.getEnd();
        
        Piece movingPiece = getPiece(start);
        
        // TODO: Consider doing sanity check outside,
        // and just requiring that the move is sane.
        if (!move.isSane(this)) {
            throw new PieceAbilityException(movingPiece + " cannot make move " + move);
        }
        
        Board result = unfrozenCopy();

        // The captured square might not be in the end square (in the case of en passant).
        

        Square capturedSquare = move.capturedSquare(this);
        if (capturedSquare != null) {
            result.placePiece(null, capturedSquare);
        }

        result.placePiece(null, start);
        if (move instanceof PromotionMove) {
            PromotionMove promotionMove = (PromotionMove) move; 
            Piece promotedPiece = new Piece(promotionMove.getPromotedType(),
                                            movingPiece.getPieceColor());
            result.placePiece(promotedPiece, end);
        } else {
            result.placePiece(movingPiece, end);
        }
        
        // TODO(jasonpr): Handle castling.

        result.setEnPassantSquare(move.enPassantSquare(this));

        result.setToMoveColor(toMoveColor.opposite());

        // Current mover cannot be checked in resulting Board.
        if (result.checked(toMoveColor)) {
            throw new InvalidMoveException();
        }

        return result.freeze();
    }
 
    /**
     * Place a piece on a specific square.
     * 
     * If another piece is already on that square, it's replaced.
     */
    public Board placePiece(Piece piece, Square square){
        assertUnfrozen();
        board[square.getFile() - 1][square.getRank() - 1] = piece;
        return this;
    }
    
    public Board setCaptured(Set<Piece> captured) {
        assertUnfrozen();
        this.captured = new HashSet<Piece>(captured);
        return this;
    }
    
    public Board setEnPassantSquare(Square square) {
        assertUnfrozen();
        this.enPassantSquare = square;
        return this;
    }
    
    public Board setToMoveColor(PieceColor color) {
        assertUnfrozen();
        this.toMoveColor = color;
        return this;
    }
    
    /**
     * Get the piece at the given square.
     */
    public Piece getPiece(Square square){
        return board[square.getFile() - 1][square.getRank() - 1];
    }
    
    public Square getEnPassantSquare(){
        return enPassantSquare;
    }

    public Board freeze() {
        frozen = true;
        return this;
    }
    
    private void assertUnfrozen() {
        if (frozen){
            throw new RuntimeException("Cannot modify a frozen Board.");
        }
    }
    
    /**
     * Get the set of sane move available to the piece on some square.
     * @param square
     * @return
     */
    public Set<Move> saneMoves(Square start) {
        Piece movingPiece = getPiece(start);
        
        if (movingPiece == null) {
            return new HashSet<Move>();
        }

        Collection<Square> candidateEnds;
        Collection<Move> candidateMoves;

        switch (movingPiece.getType()) {
            case PAWN:
                boolean isWhite = movingPiece.getPieceColor() == Piece.PieceColor.WHITE;
                Delta fwd = new Delta(0, isWhite? 1 : -1);
                Collection<Delta> deltas = new ArrayList<Delta>();
                deltas.add(fwd);
                deltas.add(fwd.scaled(2));
                deltas.add(Delta.sum(fwd, new Delta(1, 0)));
                deltas.add(Delta.sum(fwd,  new Delta(-1, 0)));

                candidateMoves = new ArrayList<Move>();
                Move candidateMove;
                for (Delta delta : deltas) {
                    try {
                        candidateMove = new Move(start, delta);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                    int endRank = candidateMove.getEnd().getRank();
                    int deltaRank = delta.getDeltaRank();
                    // Technically, we could just check that endRank in {1, 8}.
                    // Not totally sure why it feels nicer to do the extra
                    // deltaRank check.
                    if ((endRank == 8 && deltaRank == 1) ||
                        (endRank == 1 && deltaRank == -1)) {
                        // It's a promotion!
                        candidateMoves.addAll(PromotionMove.allPromotions(candidateMove));
                    } else {
                        // It's a non-promotion.
                        candidateMoves.add(candidateMove);
                    }
                }
                break;
            case KNIGHT:
                int[] rankDirs = {-1, 1};
                int[] fileDirs = {-1, 1};
                int[][] orders = {{1, 2}, {2, 1}}; 

                int dRank;
                int dFile;
                Delta delta;
                candidateMoves = new ArrayList<Move>();
                for (int rankDir : rankDirs) {
                    for (int fileDir : fileDirs) {
                        for (int[] order : orders) {
                            dRank = rankDir * order[0];
                            dFile = fileDir * order[1];
                            delta = new Delta(dFile, dRank);
                            try {
                                candidateMoves.add(new Move(start, delta));
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
                // TODO: Handle castling.
                candidateEnds = start.explore(Delta.QUEEN_DIRS, 1);
                candidateMoves = start.distributeOverEnds(candidateEnds);
                break;
            default:
                throw new RuntimeException("The piece type was not matched in the switch statement.");
        }
        return filterSane(candidateMoves);
    }
    
    private Set<Move> filterSane(Collection<Move> candidates) {
        Set<Move> saneMoves = new HashSet<Move>();
        for (Move c : candidates) {
            if (c.isSane(this)) {
                saneMoves.add(c);
            }
        }
        return saneMoves;
    }
    
    public Collection<Move> legalMoves() {
        Collection<Move> legalMoves = new ArrayList<Move>();
        for (Square start : Square.ALL) {
            for (Move saneMove : saneMoves(start)) {
                // TODO: Handle castling.
                Board result;
                try {
                    result = moveResult(saneMove);
                } catch (InvalidMoveException e) {
                    // TODO: Deal with this.
                    // This should never actually be thrown.
                    // Maybe InvalidMoveException should be a RuntimeException?
                    continue;
                }
                if (!result.checked(toMoveColor)) {
                    legalMoves.add(saneMove);
                }
            }
        }
        return legalMoves;
    }

    private Square kingSquare(PieceColor kingColor) {
        // TODO: Make this more efficient by "caching" the king's position
        // as an attribute of board.
        Piece king = new Piece(Piece.PieceType.KING, kingColor);
        
        for (Square possibleKingSquare : Square.ALL){
            if (king.equals(getPiece(possibleKingSquare))) {
                return possibleKingSquare;
            }
        }
        // Didn't return anything!
        throw new RuntimeException("There is no king of color " + kingColor + " on the board!");
        
    }
    
    public boolean checked(PieceColor kingColor) {
        Square kingSquare = kingSquare(kingColor);
        return isAttackable(kingSquare, kingColor.opposite());
    }

    public boolean isAttackable(Square target, PieceColor attackerColor) {
        for (Square attackerSquare : Square.ALL) {
            Piece attacker = getPiece(attackerSquare);
            if (attacker == null || attacker.getPieceColor() != attackerColor) {
                // No attacker on this square.
                continue;
            }
            for (Move m: saneMoves(attackerSquare)) {
                if (m.getEnd().equals(target)) {
                    return true;
                }
            }
        }
        // Nobody attacks the target.
        return false;
    }

    public PieceColor getToMoveColor() {
        return toMoveColor;
    }
}
