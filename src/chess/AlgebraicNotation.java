package chess;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import chess.Piece.Type;
import chess.exceptions.AlgebraicNotationException;

/**
 * Utility class for algebraic chess notation (AN).
 * See http://en.wikipedia.org/wiki/Algebraic_notation_(chess).
 */
public class AlgebraicNotation {
    // Map of AN letters to actual Piece.Type values.
    private static final Map<Character, Piece.Type> PIECE_NAMES;

    // Convenience map, for generating castling moves.
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
     * Create a ChessMove from its algebraic representation.
     *
     * (The algebraic representation should not include "+", "#", "!", etc.
     *
     * Behavior is unspecified if the algebraic representation is
     * more specific than is necessary on the given board (e.g.
     * "Ree6" where only "Re6" is needed).
     *
     * @param alg The algebraic notation String to be parsed.
     * @param position The ChessPosition on which the move is to be played.
     *      (The position affects how the String will be parsed.)
     * @throws AlgebraicNotationException when the given string cannot be understood
     *      as a move on the given ChessPosition.
     */
    public static ChessMove parse(String alg, ChessPosition position) throws AlgebraicNotationException {
        // TODO: Refactor the heck out of this mess!  Or, at least, comment the heck out of it.

        if (alg.equals("O-O")) {
            return new ChessMove(KING_SQUARES.get(position.getToMoveColor()), new Delta(2, 0));
        }

        if (alg.equals("O-O-O")) {
            return new ChessMove(KING_SQUARES.get(position.getToMoveColor()), new Delta(-2, 0));
        }

        // TODO: Implement promotion.

        final Type type;
        final String endStr;
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
            start = startFromClue(type, clue, end, position);
        } else {
            start = start(type, end, position);
        }

        ChessMove move = new ChessMove(start, end);

        // TODO(jasonpr): Do some more sanity checks
        if (captures == (move.capturedSquare(position) == null)) {
            throw new AlgebraicNotationException("A capture was indicated with 'x', but " +
                                                 "the indicated move doesn't perform a capture.");
        }

        return move;
    }

    /**
     * Return the only possible starting square for a move.
     * @param type The type of the moving piece.
     * @param end The move's ending square.
     * @param position The position from which the move is to be made.
     * @throws AlgebraicNotationException if there is no possible starting square.
     */
    private static Square start(Piece.Type type, Square end, ChessPosition position) throws AlgebraicNotationException {
        // TODO: Make more efficient.
        // TODO: Check uniqueness of start square (i.e. that no disambiguation
        // was necessary).
        return startFromSquares(type, Square.ALL, end, position);
    }

    /**
     * Get the only possible starting square for a move, given some rank or file clue.
     * @param type The type of the moving piece.
     * @param clue The rank or file from which to select the starting square,
     *      in ['a', 'h'] or ['1', '8'].  (This comes from, say, the 'b' in "Rbe5".)
     * @param end The move's ending square.
     * @param position The position from which the move is to be made.
     * @throws AlgebraicNotationException if there is no possible starting square.
     */
    private static Square startFromClue(Piece.Type type, char clue, Square end, ChessPosition position) throws AlgebraicNotationException {
        return startFromSquares(type, Square.line(clue), end, position);
    }

    /**
     * Get the only possible starting square for a move, given some optional starting squares.
     * @param type The type of the moving piece.
     * @param candidateStarts All the start squares to choose from.  Usually, this is the set
     *      of all Squares in a rank or file, or Squares.ALL.
     * @param end The move's ending square.
     * @param position The position from which the move is to be made.
     * @throws AlgebraicNotationException if there is no possible starting
     *      square in candidateSqarues.
     */
    private static Square startFromSquares(Piece.Type type, Iterable<Square> candidateStarts,
                                           Square end, ChessPosition position) throws AlgebraicNotationException {
        Piece movingPiece;
        ChessMove candidateMove;
        for (Square start : candidateStarts) {
            if (start.equals(end)) {
                continue;
            }
            movingPiece = position.getPiece(start);
            if (movingPiece == null || movingPiece.getType() != type ||
                movingPiece.getColor() != position.getToMoveColor()) {
                continue;
            }
            candidateMove = new ChessMove(start, end);

            if (candidateMove.isLegal(position)) {
                return start;
            }
        }
        // There is no such start square.
        throw new AlgebraicNotationException();
    }
}
