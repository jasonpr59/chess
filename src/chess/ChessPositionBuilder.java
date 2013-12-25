package chess;

import chess.Piece.Color;

public class ChessPositionBuilder {

    // A ChessPositionBuilder may only build one ChessPosition.
    // This allows them to share state, with no worry that modifying
    // the Builder will modify the Position.
    private boolean built = false;
    
    // board[file - 1][rank - 1] = the piece with specified rank and file
    // For example, board[1][7] is the piece at b8.
    private final Piece[][] board;
    private Square enPassantSquare;
    private Color toMoveColor;
    // For use in deciding whether castling is legal.
    private CastlingInfo castlingInfo;
    
    public ChessPositionBuilder() {
        board = new Piece[8][8];
        setEnPassantSquare(null);
        setToMoveColor(Piece.Color.WHITE);
        setCastlingInfo(new CastlingInfo());
    }
    
    public ChessPositionBuilder(ChessPosition source) {
        board = new Piece[8][8];
        for (Square s : Square.ALL) {
            placePiece(source.getPiece(s), s);
        }
        setEnPassantSquare(source.getEnPassantSquare());
        setToMoveColor(source.getToMoveColor());
        setCastlingInfo(source.getCastlingInfo());
    }
    
    /** Put the board in the default chess starting position. */
    public ChessPositionBuilder setupNewGame() {
        assertUnbuilt();

        // Clear out the whole board.
        for (Square sq : Square.ALL) {
            placePiece(null, sq);
        }
        // No en passant square, yet.
        setEnPassantSquare(null);
        setToMoveColor(Piece.Color.WHITE);
        setCastlingInfo(new CastlingInfo());


        // Set up the pawns
        for (int file = 1; file <= 8; file++){
            placePiece(new Piece(Piece.Type.PAWN, Piece.Color.WHITE),
                         Square.squareAt(file, 2));
            placePiece(new Piece(Piece.Type.PAWN, Piece.Color.BLACK),
                         Square.squareAt(file, 7));
        }

        // Set up the pieces
        placePiece(new Piece(Piece.Type.ROOK, Piece.Color.WHITE),
                   Square.squareAt(1, 1));
        placePiece(new Piece(Piece.Type.ROOK, Piece.Color.BLACK),
                   Square.squareAt(1, 8));

        placePiece(new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE),
                   Square.squareAt(2, 1));
        placePiece(new Piece(Piece.Type.KNIGHT, Piece.Color.BLACK),
                   Square.squareAt(2, 8));

        placePiece(new Piece(Piece.Type.BISHOP, Piece.Color.WHITE),
                   Square.squareAt(3, 1));
        placePiece(new Piece(Piece.Type.BISHOP, Piece.Color.BLACK),
                   Square.squareAt(3, 8));

        placePiece(new Piece(Piece.Type.QUEEN, Piece.Color.WHITE),
                   Square.squareAt(4, 1));
        placePiece(new Piece(Piece.Type.QUEEN, Piece.Color.BLACK),
                   Square.squareAt(4, 8));

        placePiece(new Piece(Piece.Type.KING, Piece.Color.WHITE),
                   Square.squareAt(5, 1));
        placePiece(new Piece(Piece.Type.KING, Piece.Color.BLACK),
                   Square.squareAt(5, 8));

        placePiece(new Piece(Piece.Type.BISHOP, Piece.Color.WHITE),
                   Square.squareAt(6, 1));
        placePiece(new Piece(Piece.Type.BISHOP, Piece.Color.BLACK),
                   Square.squareAt(6, 8));

        placePiece(new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE),
                   Square.squareAt(7, 1));
        placePiece(new Piece(Piece.Type.KNIGHT, Piece.Color.BLACK),
                   Square.squareAt(7, 8));

        placePiece(new Piece(Piece.Type.ROOK, Piece.Color.WHITE),
                   Square.squareAt(8, 1));
        placePiece(new Piece(Piece.Type.ROOK, Piece.Color.BLACK),
                   Square.squareAt(8, 8));

        return this;
    }
    

    private void assertUnbuilt() {
        if (built){
            throw new AssertionError("This ChessPositionBuilder already built its " +
                                     "ChessPosition, so it cannot be modified.");
        }
    }
    
    /**
     * Simple private ChessPosition implementation.
     * 
     * We use this private class so that we don't need to duplicate
     * the board in the constructor.  This allows us to just guarantee,
     * inside of ChessPositionBuilder, that we'll never modify that
     * board once we pass it into a ChessPositionImpl.
     * 
     * If we exposed this class and its constructor publicly, we would
     * run the risk of somebody passing in a board Piece[][] array, then
     * mutating that array and violating the immutability condition.
     *
     */
    private class ChessPositionImpl extends AbstractChessPosition {
        
        private final Piece[][] board;
        private final Square enPassantSquare;
        private final Piece.Color toMoveColor;
        private final CastlingInfo castlingInfo;
        
        private ChessPositionImpl(Piece[][] board, Square enPassantSquare,
                                  Piece.Color toMoveColor, CastlingInfo castlingInfo) {
            super();
            this.board = board;
            this.enPassantSquare = enPassantSquare;
            this.toMoveColor = toMoveColor;
            this.castlingInfo = castlingInfo;
        }
        
        @Override
        public Piece getPiece(Square square) {
            return board[square.getFile() - 1][square.getRank() - 1];
        }

        @Override
        public Square getEnPassantSquare() {
            return enPassantSquare;
        }

        @Override
        public Color getToMoveColor() {
            return toMoveColor;
        }

        @Override
        public CastlingInfo getCastlingInfo() {
            // TODO: Once castlingInfo is immutable,
            // remove this new CastlingInfo creation. 
            return new CastlingInfo(castlingInfo);
        }
    }
    
    public ChessPosition build() {
        assertUnbuilt();
        built = true;
        return new ChessPositionImpl(board, enPassantSquare, toMoveColor, castlingInfo);
    }
    
    /**
     * Place a piece on a square.
     * If another piece is already on that square, it is replaced.
     */
    public ChessPositionBuilder placePiece(Piece piece, Square square){
        assertUnbuilt();
        board[square.getFile() - 1][square.getRank() - 1] = piece;
        return this;
    }

    /**
     * Set the square onto which pawns move for an en-passant capture.
     * @return This ChessPositionBuilder, for daisy chaining.
     */
    public ChessPositionBuilder setEnPassantSquare(Square square) {
        assertUnbuilt();
        this.enPassantSquare = square;
        return this;
    }

    /**
     * Set the color whose move it is.
     * @return This ChessPositionBuilder, for daisy chaining.
     */
    public ChessPositionBuilder setToMoveColor(Color color) {
        assertUnbuilt();
        this.toMoveColor = color;
        return this;
    }

    
    /**
     * Set the color whose move it is.
     * @return This ChessPositionBuilder, for daisy chaining.
     */
    public ChessPositionBuilder setCastlingInfo(CastlingInfo castlingInfo) {
        assertUnbuilt();
        // TODO: Once castlingInfo is immutable,
        // remove this new CastlingInfo creation. 
        this.castlingInfo = new CastlingInfo(castlingInfo);
        return this;
    }
    
    /**
     * Take note of a move's effect on future castling abilities.
     * For example, if the move moves white's h-rook, then white
     * will no longer be able to castle kingside henceforth.
     * @return This ChessPositionBuilder, for daisy chaining.
     */
    public ChessPositionBuilder updateCastlingInfo(ChessMove move) {
        assertUnbuilt();
        this.castlingInfo.update(move);
        return this;
    }
    
    
}
