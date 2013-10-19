package chess;

import java.util.HashSet;
import java.util.Set;

import exceptions.InvalidMoveException;
import exceptions.PieceAbilityException;

public class Board {

    // board[file - 1][rank - 1] = the piece with specified rank and file
    // For example, board[1][7] is the piece at b8.
    private final Piece[][] board;
    
    private final Set<Piece> captured;
    
    private Square enPassantSquare;
    
    /**
     * Construct a new board in the default chess starting position.
     */
    public Board() {
        board = new Piece[8][8];
        
        // Set up the pawns
        for (int file = 1; file <= 8; file++){
            placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.WHITE),
                        new Square(file, 2));
            placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.BLACK),
                        new Square(file, 7));
        }
        
        // Set up the pieces
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE),
                   new Square(1, 1));
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK),
                   new Square(1, 8));
        
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE),
                   new Square(2, 1));
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK),
                   new Square(2, 8));
        
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE),
                   new Square(3, 1));
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK),
                   new Square(3, 8));
        
        placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.WHITE),
                   new Square(4, 1));
        placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.BLACK),
                   new Square(4, 8));
        
        placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.WHITE),
                   new Square(5, 1));
        placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.BLACK),
                   new Square(5, 8));
        
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE),
                   new Square(6, 1));
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK),
                   new Square(6, 8));
        
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE),
                   new Square(7, 1));
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK),
                   new Square(7, 8));
        
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE),
                   new Square(8, 1));
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK),
                   new Square(8, 8));
        
        // No captured pieces, yet.
        captured = new HashSet<Piece>();
        
        // No en passant square, yet.
        enPassantSquare = null;
    }

    /**
     * Perform a move on this board.
     * @param move A Move object designating which move should be made.
     */
    public void move(Move move) throws InvalidMoveException{
        Square start = move.getStart();

        Piece movingPiece = getPiece(start);
        
        if (!move.isSane(this)) {
            throw new PieceAbilityException(movingPiece + " cannot make move " + move);
        }
        
        // TODO(jasonpr): Finish implementing.
        
    }
 
    /**
     * Place a piece on a specific square.
     * 
     * If another piece is already on that square, it's replaced.
     */
    private void placePiece(Piece piece, Square square){
        board[square.getFile() - 1][square.getRank() - 1] = piece;
    }
    
    /**
     * Get the piece at the given square.
     */
    public Piece getPiece(Square square){
        return board[square.getFile() - 1][square.getRank() - 1];
    }

    
}
