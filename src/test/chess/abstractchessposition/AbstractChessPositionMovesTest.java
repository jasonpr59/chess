package test.chess.abstractchessposition;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import test.TestUtil;
import chess.CastlingInfo;
import chess.CastlingMove;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.Delta;
import chess.NormalChessMove;
import chess.PromotionMove;
import chess.Square;
import chess.exceptions.AlgebraicNotationException;
import chess.piece.Bishop;
import chess.piece.Knight;
import chess.piece.Piece;
import chess.piece.Queen;
import chess.piece.Rook;

/** Various tests for the AbstractChessPosition.moves() method. */
public class AbstractChessPositionMovesTest {
    @Test
    public void testNewGame() {
        // Test new game.
        ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();

        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        // Add knight moves.
        expected.add(new NormalChessMove("b1", "a3"));
        expected.add(new NormalChessMove("b1", "c3"));
        expected.add(new NormalChessMove("g1", "f3"));
        expected.add(new NormalChessMove("g1", "h3"));
        // Add pawn moves.
        Delta oneForward = new Delta(0, 1);
        Delta twoForward = new Delta(0, 2);
        for (int file = 1; file <= 8; file++) {
            expected.add(new NormalChessMove(Square.squareAt(file, 2), oneForward));
            expected.add(new NormalChessMove(Square.squareAt(file, 2), twoForward));
        }

        Collection<ChessMove> firstMoves = newGame.moves();

        TestUtil.assertSameElements(expected, firstMoves);
    }

    @Test
    public void testKingChecked() throws AlgebraicNotationException{
        ChessPositionBuilder builder = new ChessPositionBuilder();
        String[] placements = {"WKa1", "BBc3", "WRb3", "BKh8"};
        builder.placePieces(placements);
        ChessPosition whiteChecked = builder.build();

        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        // Two ways to move the king out of check.
        expected.add(new NormalChessMove("a1", "a2"));
        expected.add(new NormalChessMove("a1", "b1"));
        // Block the check with the rook.
        expected.add(new NormalChessMove("b3", "b2"));
        //Take the attacking piece.
        expected.add(new NormalChessMove("b3", "c3"));

        TestUtil.assertSameElements(expected, whiteChecked.moves());
    }

    @Test
    public void testEnPassant() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        String[] placements = {"WKc3", "BKa8", "WPg5", "BPf5"};
        builder.placePieces(placements);
        builder.setEnPassantSquare(Square.algebraic("f6"));
        ChessPosition enPassantPossible = builder.build();

        Collection<ChessMove> expected = new ArrayList<ChessMove>();

        // King moves.
        Square kingStart = Square.algebraic("c3");
        for (Delta delta : Delta.QUEEN_DIRS) {
            expected.add(new NormalChessMove(kingStart, delta));
        }

        // Pawn push.
        expected.add(new NormalChessMove("g5", "g6"));

        //Pawn en passant capture.
        expected.add(new NormalChessMove("g5", "f6"));

        TestUtil.assertSameElements(expected, enPassantPossible.moves());
    }

    @Test
    public void testCastlingLegal() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        String[] placements = {"WRa1", "WKe1", "WRh1", "BKe8"};
        builder.placePieces(placements);
        ChessPosition whiteCanCastle = builder.build();

        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        Square aRookStart = Square.algebraic("a1");
        Square hRookStart = Square.algebraic("h1");
        Square kingStart = Square.algebraic("e1");
        // a-rook and h-rook can move on file.
        for (int deltaRank = 1; deltaRank <= 7; deltaRank++) {
            Delta delta = new Delta(0, deltaRank);
            expected.add(new NormalChessMove(aRookStart, delta));
            expected.add(new NormalChessMove(hRookStart, delta));
        }
        // a-rook can move on rank until it hits the king.
        for (int deltaFile = 1; deltaFile <=3; deltaFile++) {
            expected.add(new NormalChessMove(aRookStart, new Delta(deltaFile, 0)));
        }
        // h-rook can move on rank until it hits the king.
        for (int deltaFile = -1; deltaFile >= -2; deltaFile--) {
            expected.add(new NormalChessMove(hRookStart, new Delta(deltaFile, 0)));
        }
        // king can make five one-square moves-- the non-backwards ones.
        for (Delta delta : Delta.QUEEN_DIRS) {
            if (delta.getDeltaRank() >= 0) {
                expected.add(new NormalChessMove(kingStart, delta));
            }
        }
        // king can castle on either side.
        expected.add(new CastlingMove(CastlingInfo.Side.KINGSIDE, Piece.Color.WHITE));
        expected.add(new CastlingMove(CastlingInfo.Side.QUEENSIDE, Piece.Color.WHITE));

        TestUtil.assertSameElements(expected, whiteCanCastle.moves());
    }

    @Test
    public void testPromotion() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        String[] placements = {"WPe7", "BNd8", "WKe2", "BKh8"};
        builder.placePieces(placements);
        ChessPosition canPromote = builder.build();

        final Piece[] PROMOTED_PIECES = {
                new Knight(Piece.Color.WHITE), new Bishop(Piece.Color.WHITE),
                new Rook(Piece.Color.WHITE), new Queen(Piece.Color.WHITE)};

        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        NormalChessMove pawnPush = new NormalChessMove("e7", "e8");
        NormalChessMove pawnCapture = new NormalChessMove("e7", "d8");
        for (Piece promotedPiece : PROMOTED_PIECES) {
            // Pawn push to promote.
            expected.add(new PromotionMove(pawnPush, promotedPiece));
            // Pawn capture to promote.
            expected.add(new PromotionMove(pawnCapture, promotedPiece));
        }
        // King moves.
        Square kingStart = Square.algebraic("e2");
        for (Delta delta : Delta.QUEEN_DIRS) {
            expected.add(new NormalChessMove(kingStart, delta));
        }

        TestUtil.assertSameElements(expected, canPromote.moves());
    }

    @Test
    public void testCheckmate() {
        // We'll craft a mate with the following properties:
        //  * The white king is checked.
        //  * There are some open squares next to the white king,
        //    but they're attacked by the black king.
        //  * There are some unattacked squares next to the white
        //    king, but they're occupied by other white pieces.
        //  * The king is on the edge (i.e. there are some
        //    "unattacked squares" next to the white king, but they
        //    are off the board.
        //  * The checking piece is attacked by a white piece,
        //    but that white piece is pinned to the king.
        // The result is that we have a single board such that,
        // if `moves()` gives us an empty set, we can be pretty
        // confident that `moves()` will *always* return an empty
        // set when the to-move king is mated.

        ChessPositionBuilder builder = new ChessPositionBuilder();
        String[] placements = {"WKd1", // white king, mated.
                               "BKd3", // black king, protects 2nd rank.
                               "BBe2", // black bishop, checks white king.
                               "WQc1", // white queen, gets in the way.
                               "WQe1", // white queen, pinned
                               "BRh1", // black rook, pins white queen on e1.
                              };
        builder.placePieces(placements);
        ChessPosition whiteCheckmated = builder.build();

        assertEquals(0, whiteCheckmated.moves().size());
    }

    @Test
    public void testStalemate() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        String[] placements = {"WKa6", "BKa8", "WRb1"};
        builder.placePieces(placements);
        builder.setToMoveColor(Piece.Color.BLACK);
        ChessPosition stalemate = builder.build();

        assertEquals(0, stalemate.moves().size());
    }
}
