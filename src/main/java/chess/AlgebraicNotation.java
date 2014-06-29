package main.java.chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.chess.exceptions.AlgebraicNotationException;
import main.java.chess.piece.Bishop;
import main.java.chess.piece.King;
import main.java.chess.piece.Knight;
import main.java.chess.piece.Pawn;
import main.java.chess.piece.Piece;
import main.java.chess.piece.Queen;
import main.java.chess.piece.Rook;

/**
 * Utility class for algebraic chess notation (AN).
 * See http://en.wikipedia.org/wiki/Algebraic_notation_(chess).
 */
public class AlgebraicNotation {


    private final static String typePattern = "(N|B|R|Q|K)?";
    private final static String cluePattern = "([a-h]?[1-8]?)";
    private final static String capturesPattern = "(x?)";
    private final static String endPattern = "([a-h][1-8])";
    private final static String promotionPattern = "(?:=(N|B|R|Q))?";
    private final static String checkPattern = "(\\+|#)?";

    private final static Pattern algebraic =
            Pattern.compile(typePattern + cluePattern + capturesPattern +
                            endPattern + promotionPattern + checkPattern);

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
        if (alg.equals("O-O")) {
            return new CastlingMove(CastlingMove.Side.KINGSIDE, position.getToMoveColor());
        }

        if (alg.equals("O-O-O")) {
            return new CastlingMove(CastlingMove.Side.QUEENSIDE, position.getToMoveColor());
        }

        Matcher matcher = algebraic.matcher(alg);

        if (!matcher.matches()) {
            throw new AlgebraicNotationException("Could not match move `" + alg +
                                                 "` to Algebraic Notation template");
        }

        String type = matcher.group(1);
        String clue = matcher.group(2);
        String capturesSignal = matcher.group(3);
        String endAlgebraic = matcher.group(4);
        String promotion = matcher.group(5);
        String check = matcher.group(6);

        Piece.Color color = position.getToMoveColor();

        Piece piece = piece(type, color);

        Square end = Square.algebraic(endAlgebraic);

        boolean promotes = (promotion != null);

        Square start;
        if (clue.equals("")) {
            start = start(piece.getClass(), end, position, promotes);
        } else {
            start = startFromClue(piece.getClass(), clue, end, position, promotes);
        }

        ChessMove move;

        NormalChessMove normalMove = new NormalChessMove(start, end);
        if (!promotes) {
            move = normalMove;
        } else if ("N".equals(promotion)) {
            move = new PromotionMove(normalMove, new Knight(color));
        } else if ("B".equals(promotion)) {
            move = new PromotionMove(normalMove, new Bishop(color));
        } else if ("R".equals(promotion)) {
            move = new PromotionMove(normalMove, new Rook(color));
        } else if ("Q".equals(promotion)) {
            move = new PromotionMove(normalMove, new Queen(color));
        } else {
            throw new AlgebraicNotationException("Illegal promotion type `" + promotion + "`.");
        }

        boolean captures;
        if ("x".equals(capturesSignal)) {
            captures = true;
        } else if ("".equals(capturesSignal)) {
            captures = false;
        } else {
            throw new AlgebraicNotationException(
                    "Illegal captures signal `" + capturesSignal + "`.");
        }

        boolean moveReallyCaptures = (move.capturedSquare(position) != null);
        if (moveReallyCaptures && !captures) {
            throw new AlgebraicNotationException("Capturing move has no 'x'.");
        } else if (!moveReallyCaptures && captures) {
            throw new AlgebraicNotationException("Non-capturing move has an 'x'.");
        }

        ChessPosition result = move.result(position);

        boolean isReallyCheck = result.checked(result.getToMoveColor());
        boolean isReallyCheckMate = isReallyCheck && (result.moves().size() == 0);

        if (check == null) {
            // Assert no check.
            if (isReallyCheck) {
                throw new AlgebraicNotationException("Checking move has no '+' or '#'.");
            }
        } else if ("+".equals(check)) {
            // Assert is check.
            if (!isReallyCheck) {
                throw new AlgebraicNotationException("Non-checking move has a '+'.");
            }
        } else if ("#".equals(check)) {
            // Assert is mate
            if (!isReallyCheckMate) {
                throw new AlgebraicNotationException("Non-checkmating move has a '#'.");
            }
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
    private static Square start(Class<?> type, Square end, ChessPosition position,
                                boolean promotes) throws AlgebraicNotationException {
        return startFromSquares(type, Square.ALL, end, position, promotes);
    }

    /**
     * Get the only possible starting square for a move, given some rank and/or file clue.
     * @param type The type of the moving piece.
     * @param clue The rank and/or file from which to select the starting square,
     *      in ['a', 'h'] or ['1', '8'].  (This comes from, say, the 'b' in "Rbe5",
     *      or the '8' in "R8e5", or the "c7" in "Nc7d5".
     * @param end The move's ending square.
     * @param position The position from which the move is to be made.
     * @throws AlgebraicNotationException if there is no possible starting square.
     */
    private static Square startFromClue(Class<?> type, String clue, Square end,
                                        ChessPosition position, boolean promotes)
                                                throws AlgebraicNotationException {
        Collection<Square> candidateStarts = new ArrayList<Square>();
        if (clue.length() == 1) {
            char clueChar = clue.charAt(0);
            candidateStarts.addAll(Square.line(clueChar));
        } else if (clue.length() == 2) {
            candidateStarts.add(Square.algebraic(clue));
        } else {
            throw new IllegalArgumentException("Clue must have length 1 or 2.");
        }
        return startFromSquares(type, candidateStarts, end, position, promotes);

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
                                           Square end, ChessPosition position, boolean promotes)
                                                   throws AlgebraicNotationException {
        Piece movingPiece;
        ChessMove candidateMove;

        ArrayList<Square> possibleStarts = new ArrayList<Square>();
        for (Square start : candidateStarts) {
            if (start.equals(end)) {
                continue;
            }
            movingPiece = position.getPiece(start);
            if (movingPiece == null || movingPiece.getClass() != type ||
                movingPiece.getColor() != position.getToMoveColor()) {
                continue;
            }

            NormalChessMove normalMove = new NormalChessMove(start, end);
            if (promotes) {
                // The promotedType doesn't matter, here.  We just use a queen.
                candidateMove =
                        new PromotionMove(normalMove, new Queen(position.getToMoveColor()));
            } else {
                candidateMove = normalMove;
            }

            if (candidateMove.isLegal(position)) {
                possibleStarts.add(start);
            }
        }
        if (possibleStarts.size() == 1) {
            return possibleStarts.get(0);
        } else if (possibleStarts.size() == 0) {
            throw new AlgebraicNotationException("There is no starting square for that move.");
        } else {
            throw new AlgebraicNotationException(
                    "There are multiple possible starting squares for that move!");
        }
    }

    private static Piece piece(String type, Piece.Color color) throws AlgebraicNotationException {
        if (type == null) {
            return new Pawn(color);
        } else if (type.equals("N")) {
            return new Knight(color);
        } else if (type.equals("B")) {
            return new Bishop(color);
        } else if (type.equals("R")) {
            return new Rook(color);
        } else if (type.equals("Q")) {
            return new Queen(color);
        } else if (type.equals("K")) {
            return new King(color);
        } else {
            throw new AlgebraicNotationException("Illegal piece type: `" + type + "`.");
        }
    }
}
