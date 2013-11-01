package chess;

import java.util.HashSet;
import java.util.Set;

import exceptions.InvalidMoveException;
import exceptions.PieceAbilityException;

public class Board {

    // Once frozen is true, the board becomes immutable.
    private boolean frozen = false;
    
    
    // board[file - 1][rank - 1] = the piece with specified rank and file
    // For example, board[1][7] is the piece at b8.
    private final Piece[][] board;
    
    private Set<Piece> captured;
    
    private Square enPassantSquare;
    
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
                         new Square(file, 2));
            b.placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.BLACK),
                         new Square(file, 7));
        }
        
        // Set up the pieces
        b.placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE),
                    new Square(1, 1));
        b.placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK),
                    new Square(1, 8));
        
        b.placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE),
                    new Square(2, 1));
        b.placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK),
                    new Square(2, 8));
        
        b.placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE),
                    new Square(3, 1));
        b.placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK),
                    new Square(3, 8));
        
        b.placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.WHITE),
                    new Square(4, 1));
        b.placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.BLACK),
                    new Square(4, 8));
        
        b.placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.WHITE),
                    new Square(5, 1));
        b.placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.BLACK),
                    new Square(5, 8));
        
        b.placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE),
                    new Square(6, 1));
        b.placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK),
                    new Square(6, 8));
        
        b.placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE),
                    new Square(7, 1));
        b.placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK),
                    new Square(7, 8));
        
        b.placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE),
                    new Square(8, 1));
        b.placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK),
                    new Square(8, 8));
        
        b.freeze();
        return b;
    }
    
    private Board unfrozenCopy() {
        Board b = new Board();
        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                Square sq = new Square(file, rank);
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
        result.placePiece(movingPiece, end);
        
        // TODO(jasonpr): Handle castling.
        // TODO(jasonpr): Hanlde promotion.

        result.setEnPassantSquare(move.enPassantSquare(this));

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
    
    
}
