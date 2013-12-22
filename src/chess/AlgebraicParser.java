package chess;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import chess.Piece.Type;
import exceptions.AlgebraicNotationException;

public class AlgebraicParser {
    private static final Map<Character, Piece.Type> PIECE_NAMES;
    private static final Map<Piece.Color, Square> KING_SQUARES;
    
    static {
        Map<Character, Piece.Type> pieceNames = new HashMap<Character, Piece.Type>();
        pieceNames.put('N', Piece.Type.KNIGHT);
        pieceNames.put('B', Piece.Type.BISHOP);
        pieceNames.put('R', Piece.Type.ROOK);
        pieceNames.put('Q', Piece.Type.QUEEN);
        pieceNames.put('K', Piece.Type.KING);
        PIECE_NAMES = Collections.unmodifiableMap(pieceNames);

        Map<Piece.Color, Square> kingSquares = new EnumMap<Piece.Color, Square>(Piece.Color.class);
        kingSquares.put(Piece.Color.WHITE, Square.squareAt(5, 1));
        kingSquares.put(Piece.Color.BLACK, Square.squareAt(5, 8));
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
        
        final Type type;
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
            type = Piece.Type.PAWN;
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

        Move move = new Move(start, end);

        // TODO(jasonpr): Do some more sanity checks
        if (captures == (move.capturedSquare(board) == null)) {
            throw new AlgebraicNotationException("A capture was indicated with 'x', but " +
                                                 "the indicated move doesn't perform a capture.");
        }
        
        return move;
    }
    
    
    private static Square start(Piece.Type type, Square end, Board board) throws AlgebraicNotationException {
        // TODO: Make more efficient.
        // TODO: Check uniqueness of start square (i.e. that no disambiguation
        // was necessary).
        return startFromSquares(type, Square.ALL, end, board);
    }
    
    private static Square startFromClue(Piece.Type type, char clue, Square end, Board board) throws AlgebraicNotationException {
        return startFromSquares(type, Square.line(clue), end, board);
    }
    
    private static Square startFromSquares(Piece.Type type, Iterable<Square> candidateStarts,
                                           Square end, Board board) throws AlgebraicNotationException {
        Piece movingPiece;
        Move candidateMove;
        for (Square start : candidateStarts) {
            if (start.equals(end)) {
                continue;
            }
            movingPiece = board.getPiece(start);
            if (movingPiece == null || movingPiece.getType() != type ||
                movingPiece.getColor() != board.getToMoveColor()) {
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
