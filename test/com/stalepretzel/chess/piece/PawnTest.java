package com.stalepretzel.chess.piece;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.stalepretzel.TestUtil;
import com.stalepretzel.chess.ChessMove;
import com.stalepretzel.chess.ChessPosition;
import com.stalepretzel.chess.ChessPositionBuilder;
import com.stalepretzel.chess.NormalChessMove;
import com.stalepretzel.chess.PromotionMove;
import com.stalepretzel.chess.Square;
import com.stalepretzel.chess.piece.Bishop;
import com.stalepretzel.chess.piece.King;
import com.stalepretzel.chess.piece.Knight;
import com.stalepretzel.chess.piece.Pawn;
import com.stalepretzel.chess.piece.Piece;
import com.stalepretzel.chess.piece.Queen;
import com.stalepretzel.chess.piece.Rook;

public class PawnTest {

    private ChessPositionBuilder builder;
    private ChessPosition position;

    private static Pawn WHITE_PAWN = new Pawn(Piece.Color.WHITE);
    private static Pawn BLACK_PAWN = new Pawn(Piece.Color.BLACK);

    @Before
    public void setup() {
        builder = new ChessPositionBuilder();
        builder.placePiece(new King(Piece.Color.WHITE), Square.algebraic("b2"));
        builder.placePiece(new King(Piece.Color.BLACK), Square.algebraic("b7"));
    }

    @Test
    public void testPushIsSane() {
        // We define pawn, so that we can test that pawn.isSane(move, position),
        // rather than move.isSane(position).  The first is preferable, because
        // it isolates us from errors in Move.isSane, and because it makes us
        // *sure* that we're calling the right piece's isSane method.
        // It is unfortunate, however, that it makes these tests longer, in
        // terms of line count and line length.
        builder.placePiece(WHITE_PAWN, Square.algebraic("e4"));
        position = builder.build();
        NormalChessMove push = new NormalChessMove("e4", "e5");
        assertTrue(WHITE_PAWN.isSane(push, position));
    }

    @Test
    public void testTakeTowardsAFileIsSane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e4"));
        builder.placePiece("BRd5");
        position = builder.build();
        NormalChessMove captureTowardsAFile = new NormalChessMove("e4", "d5");
        assertTrue(WHITE_PAWN.isSane(captureTowardsAFile, position));
    }

    @Test
    public void testTakeTowardsHFileIsSane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e4"));
        builder.placePiece("BRf5");
        position = builder.build();
        NormalChessMove captureTowardsHFile = new NormalChessMove("e4", "f5");
        assertTrue(WHITE_PAWN.isSane(captureTowardsHFile, position));
    }

    @Test
    public void testFriendlyCaptureIsInsane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e4"));
        builder.placePiece("WNf5");
        position = builder.build();
        NormalChessMove friendlyCapture = new NormalChessMove("e4", "f5");
        assertFalse(WHITE_PAWN.isSane(friendlyCapture, position));
    }

    @Test
    public void testSingleStepFromHomeIsSane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e2"));
        position = builder.build();
        NormalChessMove singleStep = new NormalChessMove("e2", "e3");
        assertTrue(WHITE_PAWN.isSane(singleStep, position));
    }

    @Test
    public void testDoubleStepFromHomeIsSane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e2"));
        position = builder.build();
        NormalChessMove doubleStep = new NormalChessMove("e2", "e4");
        assertTrue(WHITE_PAWN.isSane(doubleStep, position));
    }

    @Test
    public void testDoubleStepOverPieceIsInsane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e2"));
        // Blockade it with a black rook on e3.
        builder.placePiece("BRe3");
        position = builder.build();
        NormalChessMove doubleStep = new NormalChessMove("e2", "e4");
        assertFalse(WHITE_PAWN.isSane(doubleStep, position));
    }

    @Test
    public void testDoubleStepNotFromHomeIsInsane() {
        // Place pawn on non-home rank.
        builder.placePiece(WHITE_PAWN, Square.algebraic("e4"));
        position = builder.build();
        NormalChessMove doubleStep = new NormalChessMove("e4", "e6");
        assertFalse(WHITE_PAWN.isSane(doubleStep, position));
    }

    @Test
    public void testEnPassantCaptureIsSane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e5"));
        builder.placePiece(BLACK_PAWN, Square.algebraic("f5"));
        builder.setEnPassantSquare(Square.algebraic("f6"));
        position = builder.build();
        NormalChessMove enPassantCapture = new NormalChessMove("e5", "f6");
        assertTrue(WHITE_PAWN.isSane(enPassantCapture, position));
    }

    @Test
    public void testEnPassantCaptureWithPawnOnWrongSquareIsInsane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e5"));
        // Place a black pawn NOT on an adjacent file.
        builder.placePiece(BLACK_PAWN, Square.algebraic("g5"));
        builder.setEnPassantSquare(Square.algebraic("g6"));
        position = builder.build();
        NormalChessMove badEnPassantCapture = new NormalChessMove("e5", "g6");
        assertFalse(WHITE_PAWN.isSane(badEnPassantCapture, position));
    }

    @Test
    public void testPromotionIsSane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e7"));
        position = builder.build();
        NormalChessMove promotionBase = new NormalChessMove("e7", "e8");
        PromotionMove promotion = new PromotionMove(promotionBase,
                new Queen(Piece.Color.WHITE));
        assertTrue(WHITE_PAWN.isSane(promotion, position));
    }

    @Test
    public void testNonPromotingPushIsInsane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e7"));
        position = builder.build();
        NormalChessMove normalPush = new NormalChessMove("e7", "e8");
        assertFalse(WHITE_PAWN.isSane(normalPush, position));
    }

    @Test
    public void testPushCaptureIsInsane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e5"));
        // Place blockading rook.
        builder.placePiece("BRe6");
        position = builder.build();
        NormalChessMove badPushCapture = new NormalChessMove("e5", "e6");
        assertFalse(WHITE_PAWN.isSane(badPushCapture, position));
    }

    @Test
    public void testDiagonalNonCaptureIsInsane() {
        builder.placePiece(WHITE_PAWN, Square.algebraic("e5"));
        position = builder.build();
        NormalChessMove badNonCapture = new NormalChessMove("e5", "f6");
        assertFalse(WHITE_PAWN.isSane(badNonCapture, position));
    }

    @Test
    public void testBlackPushIsSane() {
        builder.placePiece(BLACK_PAWN, Square.algebraic("e5"));
        builder.setToMoveColor(Piece.Color.BLACK);
        position = builder.build();
        NormalChessMove push = new NormalChessMove("e5", "e4");
        assertTrue(BLACK_PAWN.isSane(push, position));
    }

    @Test
    public void testBlackHomeDoubleStepIsSane() {
        builder.placePiece(BLACK_PAWN, Square.algebraic("e7"));
        builder.setToMoveColor(Piece.Color.BLACK);
        position = builder.build();
        NormalChessMove doublePush = new NormalChessMove("e7", "e5");
        assertTrue(BLACK_PAWN.isSane(doublePush, position));
    }

    @Test
    public void testBlackEnPassantIsSane() {
        builder.placePiece(BLACK_PAWN, Square.algebraic("e4"));
        builder.placePiece(WHITE_PAWN, Square.algebraic("f4"));
        builder.setEnPassantSquare(Square.algebraic("f3"));
        builder.setToMoveColor(Piece.Color.BLACK);
        position = builder.build();
        NormalChessMove enPassantCapture = new NormalChessMove("e4", "f3");
        assertTrue(BLACK_PAWN.isSane(enPassantCapture, position));
    }

    @Test
    public void testBlackPromotionIsSane() {
        builder.placePiece(BLACK_PAWN, Square.algebraic("e2"));
        builder.setToMoveColor(Piece.Color.BLACK);
        position = builder.build();
        NormalChessMove promotionBase = new NormalChessMove("e2", "e1");
        PromotionMove promotion = new PromotionMove(promotionBase,
                new Queen(Piece.Color.BLACK));
        assertTrue(BLACK_PAWN.isSane(promotion, position));
    }

    @Test
    public void testSaneMovesPawnPush() {
        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        builder.placePiece("WPd4");
        expected.add(new NormalChessMove("d4", "d5"));
        position = builder.build();
        Iterable<ChessMove> sane = WHITE_PAWN.saneMoves(Square.algebraic("d4"), position);
        TestUtil.assertSameElements(expected, sane);
    }


    @Test
    public void testSaneMovesPawnPromotion() {
        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        // Pawn, promotable by push or capture.
        builder.placePiece("WPe7");
        builder.placePiece("BNf8");
        final Piece[] PROMOTED_PIECES = {
                new Knight(Piece.Color.WHITE), new Bishop(Piece.Color.WHITE),
                new Rook(Piece.Color.WHITE), new Queen(Piece.Color.WHITE)};
        for (Piece promotedPiece : PROMOTED_PIECES) {
            expected.add(new PromotionMove(new NormalChessMove("e7", "e8"),
                    promotedPiece));
            expected.add(new PromotionMove(new NormalChessMove("e7", "f8"),
                    promotedPiece));
        }
        position = builder.build();
        Iterable<ChessMove> sane = WHITE_PAWN.saneMoves(Square.algebraic("e7"), position);
        TestUtil.assertSameElements(expected, sane);
    }

    @Test
    public void testSaneMovesHomeRank() {
        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        // Pawn, can push, double push, take towards a, or take towards h.
        builder.placePiece("WPg2");
        builder.placePiece("BNf3");
        builder.placePiece("BNh3");
        expected.add(new NormalChessMove("g2", "g3"));
        expected.add(new NormalChessMove("g2", "g4"));
        expected.add(new NormalChessMove("g2", "f3"));
        expected.add(new NormalChessMove("g2", "h3"));

        position = builder.build();
        Iterable<ChessMove> sane = WHITE_PAWN.saneMoves(Square.algebraic("g2"), position);
        TestUtil.assertSameElements(expected, sane);
    }

    @Test
    public void testSaneMovesEnPassant() {
        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        // Pawn, can push or en-passant capture.
        builder.placePiece("WPg5");
        builder.placePiece("BPf5");
        builder.setEnPassantSquare(Square.algebraic("f6"));
        expected.add(new NormalChessMove("g5", "g6"));
        expected.add(new NormalChessMove("g5", "f6"));

        position = builder.build();
        Iterable<ChessMove> sane = WHITE_PAWN.saneMoves(Square.algebraic("g5"), position);
        TestUtil.assertSameElements(expected, sane);
    }
}
