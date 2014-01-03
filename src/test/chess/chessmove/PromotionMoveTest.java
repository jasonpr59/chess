package test.chess.chessmove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import test.TestUtil;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.NormalChessMove;
import chess.PromotionMove;
import chess.Square;
import chess.piece.Bishop;
import chess.piece.Knight;
import chess.piece.Piece;
import chess.piece.Queen;
import chess.piece.Rook;

/** Tests for PromotionMove. */
public class PromotionMoveTest {

    private static final ChessPosition TO_PROMOTE;
    private static final PromotionMove QUEEN_ON_F8 = new PromotionMove(
            new NormalChessMove("f7", "f8"), new Queen(Piece.Color.WHITE));
    static {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePiece("WKb2").placePiece("BKb7").placePiece("WPf7");
        TO_PROMOTE = builder.build();
    }

    @Test
    public void testEquality() {
        final NormalChessMove baseMove = new NormalChessMove("e7", "e8");
        final PromotionMove pushToQueen = new PromotionMove(baseMove,
                new Queen(Piece.Color.WHITE));

        // Equal moves.
        final PromotionMove pushToQueenAgain = new PromotionMove(baseMove,
                new Queen(Piece.Color.WHITE));

        assertEquals(pushToQueen, pushToQueenAgain);
        assertEquals(pushToQueen.hashCode(), pushToQueenAgain.hashCode());

        // Unequal by promotion type.
        final PromotionMove pushToRook = new PromotionMove(baseMove, new Rook(Piece.Color.WHITE));
        assertFalse(pushToRook.equals(pushToQueen));

        // Unequal by baseMove.
        final NormalChessMove differentFile = new NormalChessMove("f7", "f8");
        final PromotionMove pushToQueenDifferent = new PromotionMove(differentFile,
                new Queen(Piece.Color.WHITE));
        assertFalse(pushToQueenDifferent.equals(pushToQueen));

        // someNormalChessMove never equals somePromotionMove.
        assertFalse(baseMove.equals(pushToQueen));
        assertFalse(pushToQueen.equals(baseMove));
    }

    @Test
    public void testGetters() {
        final NormalChessMove basePush = new NormalChessMove("e7", "e8");
        final PromotionMove pushToQueen = new PromotionMove(basePush,
                new Queen(Piece.Color.WHITE));

        // Test getStart.
        assertEquals(Square.algebraic("e7"), pushToQueen.getStart());

        // Test getEnd.
        assertEquals(Square.algebraic("e8"), pushToQueen.getEnd());

        // getPromotedPiece.
        assertEquals(new Queen(Piece.Color.WHITE), pushToQueen.getPromotedPiece());

        // getBaseMove.
        assertEquals(basePush, pushToQueen.getBaseMove());
    }

    @Test
    public void testAllPromotions() {
        final Piece[] PROMOTION_PIECES = { new Knight(Piece.Color.WHITE),
                                           new Bishop(Piece.Color.WHITE),
                                           new Rook(Piece.Color.WHITE),
                                           new Queen(Piece.Color.WHITE) };

        final NormalChessMove basePush = new NormalChessMove("e7", "e8");
        final NormalChessMove baseCapture = new NormalChessMove("e7","f8");

        final Collection<PromotionMove> expectedPushes = new ArrayList<PromotionMove>();
        final Collection<PromotionMove> expectedCaptures = new ArrayList<PromotionMove>();

        // Test capture.
        for (Piece promoted : PROMOTION_PIECES) {
            expectedPushes.add(new PromotionMove(basePush, promoted));
            expectedCaptures.add(new PromotionMove(baseCapture, promoted));
        }

        TestUtil.assertSameElements(expectedPushes, PromotionMove.allPromotions(basePush));
        TestUtil.assertSameElements(expectedCaptures, PromotionMove.allPromotions(baseCapture));
    }

    @Test
    public void testEnPassantSquare() {
        assertNull(QUEEN_ON_F8.enPassantSquare(TO_PROMOTE));
    }

    @Test
    public void testCapturedSquareNonCapture() {
        assertNull(QUEEN_ON_F8.capturedSquare(TO_PROMOTE));
    }

    @Test
    public void testCapturedSquareCapture() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePiece("WKb2").placePiece("BKb7");
        builder.placePiece("WPf7").placePiece("BNg8");
        final ChessPosition toCapture = builder.build();
        final Square capturedSquare = Square.algebraic("g8");
        final PromotionMove captures = new PromotionMove(new NormalChessMove("f7", "g8"),
                                                         new Queen(Piece.Color.WHITE));
        assertEquals(capturedSquare, captures.capturedSquare(toCapture));
    }

    @Test
    public void testIsSaneNoPiece() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        // Don't place any piece on f7!
        builder.placePiece("WKb2").placePiece("BKb7");
        final ChessPosition noPawn = builder.build();
        assertFalse(QUEEN_ON_F8.isSane(noPawn));
    }

    @Test
    public void testIsSaneDelegation() {
        final NormalChessMove pushTof8 = new NormalChessMove("f7", "f8");

        // Returns true.
        assertTrue(QUEEN_ON_F8.isSane(TO_PROMOTE));

        // Returns false.
        assertFalse(pushTof8.isSane(TO_PROMOTE));
    }

    @Test
    public void testIsLegalLegalMove() {
        assertTrue(QUEEN_ON_F8.isLegal(TO_PROMOTE));
    }

    @Test
    public void testIsLegalKingChecked() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePiece("WKb2").placePiece("BKb7").placePiece("WPf7");
        // Check the white king.
        builder.placePiece("BRh2");
        final ChessPosition kingChecked = builder.build();
        assertFalse(QUEEN_ON_F8.isLegal(kingChecked));
    }

    @Test
    public void testIsLegalPinnedPiece() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        // Use  a black rook to pin the white pawn to the white king.
        builder.placePiece("WKb7").placePiece("WPf7").placePiece("BRh7");
        // Put the black king somewhere, so the position's valid.
        builder.placePiece("BKb2");
        final ChessPosition pawnPinned = builder.build();

        assertFalse(QUEEN_ON_F8.isLegal(pawnPinned));
    }

    @Test
    public void testPassedThrough() {
        final Collection<Square> expectedEmpty = new ArrayList<Square>();
        TestUtil.assertSameElements(expectedEmpty, QUEEN_ON_F8.passedThrough());
    }

    @Test
    public void testResultPush() {
        final ChessPosition before = TO_PROMOTE;
        final ChessPosition after = QUEEN_ON_F8.result(before);

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        expectedBuilder.placePiece("WKb2").placePiece("BKb7").placePiece("WQf8");
        expectedBuilder.setToMoveColor(Piece.Color.BLACK);
        final ChessPosition expected = expectedBuilder.build();
        assertEquals(expected, after);
    }

    @Test
    public void testResultCapture() {
        final ChessPositionBuilder beforeBuilder = new ChessPositionBuilder();
        beforeBuilder.placePiece("WKb2").placePiece("BKb7");
        beforeBuilder.placePiece("WPf7").placePiece("BNg8");
        final ChessPosition before = beforeBuilder.build();

        final PromotionMove capture = new PromotionMove(new NormalChessMove("f7", "g8"),
                                                        new Rook(Piece.Color.WHITE));
        final ChessPosition after = capture.result(before);

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        expectedBuilder.placePiece("WKb2").placePiece("BKb7");
        // The post-promotion rook.
        expectedBuilder.placePiece("WRg8");
        expectedBuilder.setToMoveColor(Piece.Color.BLACK);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, after);
    }
}

