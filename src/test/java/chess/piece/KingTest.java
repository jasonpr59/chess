package test.java.chess.piece;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import main.java.chess.CastlingMove;
import main.java.chess.ChessMove;
import main.java.chess.ChessPosition;
import main.java.chess.ChessPositionBuilder;
import main.java.chess.Delta;
import main.java.chess.NormalChessMove;
import main.java.chess.Square;
import main.java.chess.piece.King;
import main.java.chess.piece.Piece;

import org.junit.Before;
import org.junit.Test;

import test.java.TestUtil;

public class KingTest {
    private static King WHITE_KING = new King(Piece.Color.WHITE);
    private static King BLACK_KING = new King(Piece.Color.BLACK);

    ChessPositionBuilder builder;
    ChessPosition position;

    @Before
    public void setup() {
        builder = new ChessPositionBuilder();
    }

    @Test
    public void testNonCaptureIsSane() {
        builder.placePiece("WKb2").placePiece("BKb7");
        position = builder.build();

        // As in QueenTest, we only test four directions.
        final Square whiteKingStart = Square.algebraic("b2");
        final NormalChessMove north = new NormalChessMove(whiteKingStart, Delta.NORTH);
        assertTrue(WHITE_KING.isSane(north, position));
        final NormalChessMove east = new NormalChessMove(whiteKingStart, Delta.EAST);
        assertTrue(WHITE_KING.isSane(east, position));
        final NormalChessMove southWest = new NormalChessMove(whiteKingStart, Delta.SOUTH_WEST);
        assertTrue(WHITE_KING.isSane(southWest, position));
        final NormalChessMove southEast = new NormalChessMove(whiteKingStart, Delta.SOUTH_EAST);
        assertTrue(WHITE_KING.isSane(southEast, position));
    }

    @Test
    public void testCaptureIsSane() {
        builder.placePiece("WKb2").placePiece("BKb7");
        builder.placePiece("BNb3");
        position = builder.build();

        final NormalChessMove capture = new NormalChessMove("b2", "b3");
        assertTrue(WHITE_KING.isSane(capture, position));
    }

    @Test
    public void testFriendlyCaptureIsInsane() {
        builder.placePiece("WKb2").placePiece("BKb7");
        builder.placePiece("WNb3");
        position = builder.build();

        final NormalChessMove friendlyCapture = new NormalChessMove("b2", "b3");
        assertFalse(WHITE_KING.isSane(friendlyCapture, position));
    }

    @Test
    public void testMoveTooFarIsInsane() {
        builder.placePiece("WKb2").placePiece("BKb7");
        position = builder.build();

        final NormalChessMove tooFarOver = new NormalChessMove("b2", "d2");
        assertFalse(WHITE_KING.isSane(tooFarOver, position));
        final NormalChessMove tooFarUp = new NormalChessMove("b2", "b4");
        assertFalse(WHITE_KING.isSane(tooFarUp, position));
    }

    @Test
    public void testWhiteCastlingIsSane() {
        builder.placePiece("WKe1").placePiece("BKe8");
        builder.placePiece("WRa1").placePiece("WRh1");
        position = builder.build();

        CastlingMove castles;
        for (CastlingMove.Side side : CastlingMove.Side.values()) {
            castles = new CastlingMove(side, Piece.Color.WHITE);
            assertTrue(WHITE_KING.isSane(castles, position));
        }
    }

    @Test
    public void testBlackCastlingIsSane() {
        builder.placePiece("WKe1").placePiece("BKe8");
        builder.placePiece("BRa8").placePiece("BRh8");
        builder.setToMoveColor(Piece.Color.BLACK);
        position = builder.build();

        CastlingMove castles;
        for (CastlingMove.Side side : CastlingMove.Side.values()) {
            castles = new CastlingMove(side, Piece.Color.BLACK);
            assertTrue(BLACK_KING.isSane(castles, position));
        }
    }

    @Test
    public void testCastlingWhenObstructedIsInsane() {
        builder.placePiece("WKe1").placePiece("BKe8");
        builder.placePiece("WRh1");
        // Obstruct white's kingside castling.
        builder.placePiece("WBf1");
        position = builder.build();

        final CastlingMove castles = new CastlingMove(CastlingMove.Side.KINGSIDE,
                Piece.Color.WHITE);
        assertFalse(WHITE_KING.isSane(castles, position));
    }

    @Test
    public void testCastlingWhenNoLongerAllowedIsInsane() {
        builder.placePiece("WKe1").placePiece("BKe8");
        builder.placePiece("WRh1");
        // It looks like we could castle, but we'll make it impossible.
        final NormalChessMove makeWhiteKingCastleImpossible = new NormalChessMove("h2", "h1");
        builder.updateCastlingInfo(makeWhiteKingCastleImpossible);
        position = builder.build();

        final CastlingMove castles = new CastlingMove(CastlingMove.Side.KINGSIDE,
                Piece.Color.WHITE);
        assertFalse(WHITE_KING.isSane(castles, position));
    }

    @Test
    public void testSaneMoves() {
        builder.placePiece("WKe1").placePiece("BKe8");
        builder.placePiece("WRa1").placePiece("WRh1");
        // Note that the black queen's checks don't affect which moves are
        // sane... only which moves are legal.
        builder.placePiece("BQe2").placePiece("WQf2");
        position = builder.build();

        final Collection<ChessMove> expected = new ArrayList<ChessMove>();
        Square kingStart = Square.algebraic("e1");
        expected.add(new NormalChessMove(kingStart, Delta.WEST));
        expected.add(new NormalChessMove(kingStart, Delta.NORTH_WEST));
        expected.add(new NormalChessMove(kingStart, Delta.NORTH));
        expected.add(new NormalChessMove(kingStart, Delta.EAST));
        expected.add(new CastlingMove(CastlingMove.Side.KINGSIDE, Piece.Color.WHITE));
        expected.add(new CastlingMove(CastlingMove.Side.QUEENSIDE, Piece.Color.WHITE));

        final Iterable<ChessMove> sane = WHITE_KING.saneMoves(kingStart, position);
        TestUtil.assertSameElements(expected, sane);
    }
}
