package chess;

import chess.exceptions.AlgebraicNotationException;
import chess.piece.Bishop;
import chess.piece.King;
import chess.piece.Knight;
import chess.piece.Pawn;
import chess.piece.Piece;
import chess.piece.Queen;
import chess.piece.Rook;

/**
 * Utility class for algebraic chess notation (AN).
 * See http://en.wikipedia.org/wiki/Algebraic_notation_(chess).
 */
public class AlgebraicNotation {
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
            return new CastlingMove(CastlingInfo.Side.KINGSIDE, position.getToMoveColor());
        }

        if (alg.equals("O-O-O")) {
            return new CastlingMove(CastlingInfo.Side.QUEENSIDE, position.getToMoveColor());
        }

        // TODO: Implement promotion.

        final String endStr;
        final boolean captures;

        int startFront = 1;
        int startBack;

        // Get piece type.
        char typeChar = alg.charAt(0);
        Piece piece;
        Piece.Color color = position.getToMoveColor();
        if (typeChar == 'N') {
            piece = new Knight(color);
        } else if (typeChar == 'B') {
            piece = new Bishop(color);
        } else if (typeChar == 'R') {
            piece = new Rook(color);
        } else if (typeChar == 'Q') {
            piece = new Queen(color);
        } else if (typeChar == 'K') {
            piece = new King(color);
        } else {
            piece = new Pawn(color);
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
            start = startFromClue(piece.getClass(), clue, end, position);
        } else {
            start = start(piece.getClass(), end, position);
        }

        NormalChessMove move = new NormalChessMove(start, end);

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
    private static Square start(Class<?> type, Square end, ChessPosition position) throws AlgebraicNotationException {
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
    private static Square startFromClue(Class<?> type, char clue, Square end, ChessPosition position) throws AlgebraicNotationException {
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
    private static Square startFromSquares(Class<?> type, Iterable<Square> candidateStarts,
                                           Square end, ChessPosition position) throws AlgebraicNotationException {
        Piece movingPiece;
        ChessMove candidateMove;
        for (Square start : candidateStarts) {
            if (start.equals(end)) {
                continue;
            }
            movingPiece = position.getPiece(start);
            if (movingPiece == null || movingPiece.getClass() != type ||
                movingPiece.getColor() != position.getToMoveColor()) {
                continue;
            }
            candidateMove = new NormalChessMove(start, end);

            if (candidateMove.isLegal(position)) {
                return start;
            }
        }
        // There is no such start square.
        throw new AlgebraicNotationException();
    }
}
