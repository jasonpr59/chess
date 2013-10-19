package chess;

import java.util.HashSet;
import java.util.Set;

import exceptions.InvalidMoveException;

public class Board {

    // board[file - 1][rank - 1] = the piece with specified rank and file
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
            placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.WHITE), file, 2);
            placePiece(new Piece(Piece.PieceType.PAWN, Piece.PieceColor.BLACK), file, 7);
        }
        
        // Set up the pieces
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE), 1, 1);
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK), 1, 8);
        
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE), 2, 1);
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK), 2, 8);
        
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE), 3, 1);
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK), 3, 8);
        
        placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.WHITE), 4, 1);
        placePiece(new Piece(Piece.PieceType.QUEEN, Piece.PieceColor.BLACK), 4, 8);
        
        placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.WHITE), 5, 1);
        placePiece(new Piece(Piece.PieceType.KING, Piece.PieceColor.BLACK), 5, 8);
        
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.WHITE), 6, 1);
        placePiece(new Piece(Piece.PieceType.BISHOP, Piece.PieceColor.BLACK), 6, 8);
        
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.WHITE), 7, 1);
        placePiece(new Piece(Piece.PieceType.KNIGHT, Piece.PieceColor.BLACK), 7, 8);
        
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.WHITE), 8, 1);
        placePiece(new Piece(Piece.PieceType.ROOK, Piece.PieceColor.BLACK), 8, 8);
        
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
        Square end = move.getEnd();
        
        Piece movingPiece = getPiece(start.getFile(), start.getRank());
        
        if (!movingPiece.hasMoveAbility(start, end)) {
            throw new PieceAbilityException(movingPiece + " cannot move from " + start + " to " + end);
        }
        
    }
 
    private void placePiece(Piece piece, int file, int rank){
        board[file - 1][rank - 1] = piece;
    }
    
    private Piece getPiece(int file, int rank){
        return board[file - 1][rank - 1];
    }

    
}
