package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import chess.Piece.PieceColor;

/** A chess board at a specific position. */
public class Board {
    // Once frozen is true, the board becomes immutable.
    private boolean frozen = false;
    
    // board[file - 1][rank - 1] = the piece with specified rank and file
    // For example, board[1][7] is the piece at b8.
    private final Piece[][] board;
    private Square enPassantSquare;
    private PieceColor toMoveColor;
    // For use in deciding whether castling is legal.
    private CastlingInfo castlingInfo;
    
    /** Construct a new board with no pieces placed. */
    private Board() {
        board = new Piece[8][8];
        // No en passant square, yet.
        enPassantSquare = null;
        castlingInfo = new CastlingInfo();
    }
    
    /**
     * Create an unfrozen copy of a board.
     *
     * Unfrozen boards are mutable: pieces can be placed/removed,
     * the toMoveColor can be changed, etc.  We use frozen boards
     * wherever possible, because they are immutable.  But, to create
     * a board with all the right properties, we need to create a board
     * and mutate it.  And, when that board needs to be similar to an
     * existing board, it makes sense to create an unfrozen copy from
     * which to work.
     */
    private Board(Board source) {
        this();
        for (Square sq : Square.ALL) {
            placePiece(source.getPiece(sq), sq);
        }
        enPassantSquare = source.getEnPassantSquare();
        toMoveColor = source.getToMoveColor();
        castlingInfo = new CastlingInfo(source.getCastlingInfo());
    }

    /** Construct a new board in the default chess starting position. */
    public static Board newGame() {
        Board b = new Board();
        b.setupNewGame();
        return b.freeze();
    }

    /** Put the board in the default chess starting position. */
    private void setupNewGame() {
        assertUnfrozen();

        // Clear out the whole board.
        for (Square sq : Square.ALL) {
            placePiece(null, sq);
        }
        // No en passant square, yet.
        enPassantSquare = null;
        castlingInfo = new CastlingInfo();
        
        
        // Set up the pawns
        for (int file = 1; file <= 8; file++){
            placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.WHITE),
                         Square.squareAt(file, 2));
            placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.BLACK),
                         Square.squareAt(file, 7));
        }
        
        // Set up the pieces
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE),
                   Square.squareAt(1, 1));
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK),
                   Square.squareAt(1, 8));
        
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE),
                   Square.squareAt(2, 1));
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK),
                   Square.squareAt(2, 8));
        
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE),
                   Square.squareAt(3, 1));
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK),
                   Square.squareAt(3, 8));
        
        placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.WHITE),
                   Square.squareAt(4, 1));
        placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.BLACK),
                   Square.squareAt(4, 8));
        
        placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.WHITE),
                   Square.squareAt(5, 1));
        placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.BLACK),
                   Square.squareAt(5, 8));
        
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE),
                   Square.squareAt(6, 1));
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK),
                   Square.squareAt(6, 8));
        
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE),
                   Square.squareAt(7, 1));
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK),
                   Square.squareAt(7, 8));
        
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE),
                   Square.squareAt(8, 1));
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK),
                   Square.squareAt(8, 8));
        
        setToMoveColor(Piece.PieceColor.WHITE);
    }
    
    /**
     * Get the board that results from a move.
     * @param move A Move object designating which move should be made.
     * @return A frozen board that is the result of making the move on
     *     this board.
     */
    public Board moveResult(Move move){
        Square start = move.getStart();
        Square end = move.getEnd();
        
        Piece movingPiece = getPiece(start);
        
        Board result = new Board(this);

        // The captured square might not be in the end square (in the case of en passant).
        Square capturedSquare = move.capturedSquare(this);
        if (capturedSquare != null) {
            result.placePiece(null, capturedSquare);
        }
        
        if (move.isCastling(this)) {
            // Place the rook in its new spot.
            Square rookEnd= start.plus(move.getDelta().unitized());
            result.placePiece(new Piece(Piece.PieceType.ROOK, toMoveColor), rookEnd);
            
            // Remove the old rook.
            Square rookStart;
            if (move.getDelta().getDeltaFile() > 0) {
                // King Castle
                rookStart = start.plus(new Delta(3, 0));
            } else {
                // Queen Castle
                rookStart = start.plus(new Delta(-4, 0));
            }
            result.placePiece(null, rookStart);
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
        
        result.setEnPassantSquare(move.enPassantSquare(this));
        result.setToMoveColor(toMoveColor.opposite());

        // Keep track of whether castling will be allowable
        // in future moves.
        result.updateCastlingInfo(move);

        return result.freeze();
    }
 
    /**
     * Place a piece on a square.
     * If another piece is already on that square, it is replaced.
     */
    private Board placePiece(Piece piece, Square square){
        assertUnfrozen();
        board[square.getFile() - 1][square.getRank() - 1] = piece;
        return this;
    }
    
    /**
     * Set the square onto which pawns move for an en-passant capture.
     * @return This Board, for daisy chaining.
     */
    private Board setEnPassantSquare(Square square) {
        assertUnfrozen();
        this.enPassantSquare = square;
        return this;
    }
    
    /**
     * Set the color whose move it is.
     * @return This Board, for daisy chaining.
     */
    private Board setToMoveColor(PieceColor color) {
        assertUnfrozen();
        this.toMoveColor = color;
        return this;
    }
    
    /**
     * Take note of a move's effect on future castling abilities.
     * For example, if the move moves white's h-rook, then white
     * will no longer be able to castle kingside henceforth.
     * @return This Board, for daisy chaining.
     */
    private Board updateCastlingInfo(Move move) {
        assertUnfrozen();
        this.castlingInfo.update(move);
        return this;
    }

    /** Get the piece at a square, or null if the square is empty. */
    public Piece getPiece(Square square){
        return board[square.getFile() - 1][square.getRank() - 1];
    }

    /** Return whether a square is empty. */
    public boolean isEmpty(Square square) {
        return getPiece(square) == null;
    }

    /** Get the en-passant square, or null if there isn't one.*/
    public Square getEnPassantSquare(){
        return enPassantSquare;
    }

    public PieceColor getToMoveColor() {
        return toMoveColor;
    }

    public CastlingInfo getCastlingInfo() {
        // FIXME: This is a pretty bad rep exposure:
        // CastlingInfo is mutable.
        return castlingInfo;
    }
    
    
    /**
     * Freeze the board, so it becomes immutable.
     * @return This Board, for daisy chaining.
     */
    private Board freeze() {
        frozen = true;
        return this;
    }
    
    private void assertUnfrozen() {
        if (frozen){
            throw new AssertionError("Cannot modify a frozen Board.");
        }
    }
    
    /** Get the set of sane moves available to the piece on a square. */
    public Iterable<Move> saneMoves(Square start) {
        Piece movingPiece = getPiece(start);
        
        if (movingPiece == null) {
            // No piece at that square = no available moves!
            return new HashSet<Move>();
        }

        Collection<Square> candidateEnds;
        Collection<Move> candidateMoves;

        switch (movingPiece.getType()) {
            case PAWN:
                boolean isWhite = movingPiece.getPieceColor() == Piece.PieceColor.WHITE;
                Delta fwd = new Delta(0, isWhite? 1 : -1);
                Collection<Delta> candidateDeltas = new ArrayList<Delta>();
                // There are at most four possible pawn moves (ignoring promotion choices).
                // Just try them all!
                candidateDeltas.add(fwd);
                candidateDeltas.add(fwd.scaled(2));
                candidateDeltas.add(Delta.sum(fwd, new Delta(1, 0)));
                candidateDeltas.add(Delta.sum(fwd,  new Delta(-1, 0)));

                candidateMoves = new ArrayList<Move>();
                Move candidateMove;
                for (Delta delta : candidateDeltas) {
                    try {
                        candidateMove = new Move(start, delta);
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
                candidateMoves = new ArrayList<Move>();
                for (int rankDir : rankDirs) {
                    for (int fileDir : fileDirs) {
                        for (int[] alignment : alignments) {
                            dRank = rankDir * alignment[0];
                            dFile = fileDir * alignment[1];
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
                candidateEnds = start.explore(Delta.QUEEN_DIRS, 1);
                if (start.getFile() == 5) {
                    // There's a decent chance that the king's in its home square,
                    // and a zero chance that a two-square hop along a rank will
                    // put us off the board.
                    candidateEnds.add(start.plus(new Delta(2, 0)));
                    candidateEnds.add(start.plus(new Delta(-2, 0)));
                }
                candidateMoves = start.distributeOverEnds(candidateEnds);
                break;
            default:
                throw new RuntimeException("The piece type was not matched in the switch statement.");
        }
        return filterSane(candidateMoves);
    }

    /** Return the subset of sane moves from a set of moves. */
    private Collection<Move> filterSane(Collection<Move> candidates) {
        Set<Move> saneMoves = new HashSet<Move>();
        for (Move c : candidates) {
            if (c.isSane(this)) {
                saneMoves.add(c);
            }
        }
        return saneMoves;
    }

    /** Return all legal moves. */
    public Collection<Move> legalMoves() {
        Collection<Move> legalMoves = new ArrayList<Move>();
        for (Square start : Square.ALL) {
            for (Move saneMove : saneMoves(start)) {
                if (saneMove.isLegal(this)) {
                    legalMoves.add(saneMove);
                }
            }
        }
        return legalMoves;
    }

    /** Return the square that the king of some color occupies. */
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

    /** Return whether the king of some color is in check. */
    public boolean checked(PieceColor kingColor) {
        Square kingSquare = kingSquare(kingColor);
        Board trialBoard;
        if (toMoveColor == kingColor) {
            // Act as though it's the other side's turn, to see if they could attack the king.
            trialBoard = new Board(this).setToMoveColor(toMoveColor.opposite()).freeze();
        } else {
            // It's the other color's turn, so see if they can attack this king.
            trialBoard = this;
        }
        return trialBoard.isAttackable(kingSquare);
    }

    /**
     * Return whether the given square is currently under attack.
     * A square is under attack if a piece of the toMoveColor is
     * attacking it.
     * TODO: Consider allowing caller to specify attackerColor.   
     */
    public boolean isAttackable(Square target) {
        Piece.PieceColor attackerColor = toMoveColor;
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

    /** Return whether the king is unmoved and the h-rook is unmoved. */
    public boolean kingCastlePiecesReady(Piece.PieceColor color) {
        return castlingInfo.kingCastlePiecesReady(color);
    }

    /** Return whether the king is unmoved and the a-rook is unmoved. */
    public boolean queenCastlePiecesReady(Piece.PieceColor color) {
        return castlingInfo.queenCastlePiecesReady(color);
    }
}
