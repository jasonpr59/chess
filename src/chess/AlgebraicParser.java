package chess;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import chess.Piece.PieceColor;
import chess.Piece.PieceType;
import exceptions.AlgebraicNotationException;

public class AlgebraicParser {
    private static final Map<Character, Piece.PieceType> PIECE_NAMES;
    private static final Map<Piece.PieceColor, Square> KING_SQUARES;
    
    static {
        Map<Character, Piece.PieceType> pieceNames = new HashMap<Character, Piece.PieceType>();
        pieceNames.put('N', Piece.PieceType.KNIGHT);
        pieceNames.put('B', Piece.PieceType.BISHOP);
        pieceNames.put('R', Piece.PieceType.ROOK);
        pieceNames.put('Q', Piece.PieceType.QUEEN);
        pieceNames.put('K', Piece.PieceType.KING);
        PIECE_NAMES = Collections.unmodifiableMap(pieceNames);

        Map<Piece.PieceColor, Square> kingSquares = new EnumMap<Piece.PieceColor, Square>(Piece.PieceColor.class);
        kingSquares.put(Piece.PieceColor.WHITE, Square.squareAt(5, 1));
        kingSquares.put(Piece.PieceColor.BLACK, Square.squareAt(5, 8));
        KING_SQUARES = Collections.unmodifiableMap(kingSquares);
    }
    
    /**
     * Create a move from its algebraic representation.
     * 
     * (The algebraic representation should not include "#", "##", "!", etc.
     * 
     * Behavior is unspecified if the algebraic representation is
     * more specific than is necessary on the given board (e.g.
     * "Ree6" where only "Re6" is needed).
     * 
     * @param alg
     * @param board
     * @return
     * @throws AlgebraicNotationException 
     */
    public static Move parseAlgebraic(String alg, Board board) throws AlgebraicNotationException {
        if (alg.equals("O-O")) {
            return new Move(KING_SQUARES.get(board.getToMoveColor()), new Delta(2, 0));
        }
        
        if (alg.equals("O-O-O")) {
            return new Move(KING_SQUARES.get(board.getToMoveColor()), new Delta(-2, 0));
        }
        
        // TODO: Implement promotion.
        
        final PieceType type;
        final String endStr;
        // TODO: Check that, if captures, then capture occurs.
        final boolean captures;
        
        int startFront;
        int startBack;
        
        // Get piece type.
        char typeChar = alg.charAt(0);
        if (PIECE_NAMES.containsKey(typeChar)) {
            type = PIECE_NAMES.get(typeChar);
            startFront = 1;
        } else {
            type = Piece.PieceType.PAWN;
            startFront = 0;
        }
        
        // Get end square.
        endStr = alg.substring(alg.length() - 2, alg.length());
        Square end = Square.algebraic(endStr);
        
        // Get captures
        int capturesIndex = alg.length() - 3;
        if (capturesIndex >= 0 && alg.charAt(capturesIndex) == 'x') {
            captures = true;
            startBack = capturesIndex;
        } else {
            captures = false;
            startBack = capturesIndex + 1;
        }
        
        Square start;
        int startLen = startBack - startFront;
        if (startLen == 2){
            // Have the square.
            start = Square.algebraic(alg.substring(startFront, startBack));
        } else if (startLen == 1) {
            char clue = alg.charAt(startFront);
            start = startFromClue(type, clue, end, board);
        } else {
            start = start(type, end, board);
        }

        // TODO(jasonpr): Do some sanity checks.
        return new Move(start, end);
        
        
        
    }
    
    
    private static Square start(Piece.PieceType type, Square end, Board board) throws AlgebraicNotationException {
        // TODO: Make more efficient.
        // TODO: Check uniqueness of start square (i.e. that no disambiguation
        // was necessary).
        return startFromSquares(type, Square.ALL, end, board);
    }
    
    private static Square startFromClue(Piece.PieceType type, char clue, Square end, Board board) throws AlgebraicNotationException {
        return startFromSquares(type, Square.line(clue), end, board);
    }
    
    private static Square startFromSquares(Piece.PieceType type, Iterable<Square> candidateStarts,
                                           Square end, Board board) throws AlgebraicNotationException {
        Piece movingPiece;
        Move candidateMove;
        for (Square start : candidateStarts) {
            if (start.equals(end)) {
                continue;
            }
            movingPiece = board.getPiece(start);
            if (movingPiece == null || movingPiece.getType() != type ||
                movingPiece.getPieceColor() != board.getToMoveColor()) {
                continue;
            }
            candidateMove = new Move(start, end);
            
            if (candidateMove.isLegal(board)) {
                return start;
            }
        }
        // There is no such start square.
        throw new AlgebraicNotationException();
    }
}
