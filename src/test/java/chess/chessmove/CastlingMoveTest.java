package test.java.chess.chessmove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import main.java.chess.CastlingMove;
import main.java.chess.ChessPosition;
import main.java.chess.ChessPositionBuilder;
import main.java.chess.NormalChessMove;
import main.java.chess.Square;
import main.java.chess.piece.Piece;

import org.junit.Test;

import test.java.TestUtil;

/** Tests for CastlingMove. */
public class CastlingMoveTest {

    private static final CastlingMove WHITE_KING_CASTLE =
            new CastlingMove(CastlingMove.Side.KINGSIDE, Piece.Color.WHITE);
    private static final CastlingMove WHITE_QUEEN_CASTLE =
            new CastlingMove(CastlingMove.Side.QUEENSIDE, Piece.Color.WHITE);
    private static final CastlingMove BLACK_KING_CASTLE =
            new CastlingMove(CastlingMove.Side.KINGSIDE, Piece.Color.BLACK);
    private static final CastlingMove BLACK_QUEEN_CASTLE =
            new CastlingMove(CastlingMove.Side.QUEENSIDE, Piece.Color.BLACK);

    // "Utility" moves for "knocking out" different castling possibilities.
    // (To be used with `updateCastlingInfo(ChessMove)`.)
    private static final NormalChessMove KNOCKOUT_WHITE_CASTLE =
            new NormalChessMove("e1", "e2");
    private static final NormalChessMove KNOCKOUT_BLACK_CASTLE =
            new NormalChessMove("e8", "e7");

    // A ChessPosition that allows any of the four CastlingMoves to be made,
    // as long is it's the correct player's turn.
    // One for white-to-move...
    private static final ChessPosition CASTLING_POSITION_WHITE;
    // ...and one for black-to-move-
    private static final ChessPosition CASTLING_POSITION_BLACK;


    static {
        final ChessPositionBuilder castlingBuilder = new ChessPositionBuilder();
        final String[] placements = {"WKe1", "BKe8", "WRa1", "BRa8", "WRh1", "BRh8"};
        castlingBuilder.placePieces(placements);
        CASTLING_POSITION_WHITE = castlingBuilder.build();
        CASTLING_POSITION_BLACK = new ChessPositionBuilder(CASTLING_POSITION_WHITE)
                .flipToMoveColor().build();
    }

    @Test
    public void testEquality() {
        final CastlingMove whiteKingCastleAgain =
                new CastlingMove(CastlingMove.Side.KINGSIDE, Piece.Color.WHITE);
        assertEquals(WHITE_KING_CASTLE, whiteKingCastleAgain);
        assertEquals(WHITE_KING_CASTLE.hashCode(), whiteKingCastleAgain.hashCode());

        assertFalse(WHITE_KING_CASTLE.equals(BLACK_KING_CASTLE));

        assertFalse(WHITE_KING_CASTLE.equals(WHITE_QUEEN_CASTLE));

        final NormalChessMove castlishNormalMove = new NormalChessMove("e5", "g5");
        assertFalse(WHITE_KING_CASTLE.equals(castlishNormalMove));
    }

    @Test
    public void testGetters() {
        // Test getColor.
        assertEquals(Piece.Color.WHITE, WHITE_KING_CASTLE.getColor());
        // Test getSide.
        assertEquals(CastlingMove.Side.KINGSIDE, WHITE_KING_CASTLE.getSide());
    }

    @Test
    public void testCalculatedGetters() {
        // Test getStart.
        assertEquals(Square.algebraic("e1"), WHITE_KING_CASTLE.getStart());
        assertEquals(Square.algebraic("e8"), BLACK_QUEEN_CASTLE.getStart());

        // Test getEnd().
        assertEquals(Square.algebraic("g1"), WHITE_KING_CASTLE.getEnd());
        assertEquals(Square.algebraic("c8"), BLACK_QUEEN_CASTLE.getEnd());

        // Test getRookStart.
        assertEquals(Square.algebraic("h1"), WHITE_KING_CASTLE.getRookStart());
        assertEquals(Square.algebraic("a8"), BLACK_QUEEN_CASTLE.getRookStart());

        // Test getRookEnd
        assertEquals(Square.algebraic("f1"), WHITE_KING_CASTLE.getRookEnd());
        assertEquals(Square.algebraic("d8"), BLACK_QUEEN_CASTLE.getRookEnd());
    }

    @Test
    public void testFromNormalMove() {
        NormalChessMove likeWhiteKingCastle = new NormalChessMove("e1", "g1");
        assertEquals(WHITE_KING_CASTLE, CastlingMove.fromNormalMove(likeWhiteKingCastle));
    }

    @Test
    public void testEnPassantSquare() {
        assertNull(WHITE_KING_CASTLE.enPassantSquare(CASTLING_POSITION_WHITE));
    }

    @Test
    public void testCapturedSquare() {
        assertNull(BLACK_QUEEN_CASTLE.capturedSquare(CASTLING_POSITION_WHITE));
    }

    @Test
    public void testIsInsaneNoMovingPiece() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePiece("WKe2").placePiece("BKe7");
        // Make the CastlingInfo accurately reflect the board's state,
        // just to be safe.
        builder.updateCastlingInfo(KNOCKOUT_WHITE_CASTLE);
        builder.updateCastlingInfo(KNOCKOUT_BLACK_CASTLE);
        final ChessPosition kingMoved = builder.build();
        assertFalse(WHITE_KING_CASTLE.isSane(kingMoved));
    }

    @Test
    public void testIsSaneDelegation() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WRa1", "WBc1", "WKe1", "WRh1", "BKe8"};
        builder.placePieces(placements);
        builder.updateCastlingInfo(KNOCKOUT_BLACK_CASTLE);
        final ChessPosition onlyKingCastle = builder.build();
        // Result is true.
        assertTrue(WHITE_KING_CASTLE.isSane(onlyKingCastle));
        // Result is false.
        assertFalse(WHITE_QUEEN_CASTLE.isSane(onlyKingCastle));
    }

    @Test
    public void testIsLegalLegalMove() {
        assertTrue(WHITE_KING_CASTLE.isLegal(CASTLING_POSITION_WHITE));
    }

    @Test
    public void testCastleOutOfCheckIsIllegal() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WRa1", "WKe1", "WRh1", "BKe8", "BRe7"};
        builder.placePieces(placements);
        builder.updateCastlingInfo(KNOCKOUT_BLACK_CASTLE);
        final ChessPosition whiteChecked = builder.build();

        assertFalse(WHITE_KING_CASTLE.isLegal(whiteChecked));
    }

    @Test
    public void testCastleThroughCheckIsIllegal() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WRa1", "WKe1", "WRh1", "BKe8", "BRf7"};
        builder.placePieces(placements);
        builder.updateCastlingInfo(KNOCKOUT_BLACK_CASTLE);
        final ChessPosition blackAttacksF1 = builder.build();

        assertFalse(WHITE_KING_CASTLE.isLegal(blackAttacksF1));
    }

    @Test
    public void testCastleIntoCheckIsIllegal() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WRa1", "WKe1", "WRh1", "BKe8", "BRg7"};
        builder.placePieces(placements);
        builder.updateCastlingInfo(KNOCKOUT_BLACK_CASTLE);
        final ChessPosition blackAttacksG1 = builder.build();

        assertFalse(WHITE_KING_CASTLE.isLegal(blackAttacksG1));
    }

    @Test
    public void testInsaneCastleIsIllegal() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WRa1", "WBc1", "WKe1", "WRh1", "BKe8"};
        builder.placePieces(placements);
        builder.updateCastlingInfo(KNOCKOUT_BLACK_CASTLE);
        final ChessPosition onlyKingCastle = builder.build();

        assertFalse(WHITE_QUEEN_CASTLE.isLegal(onlyKingCastle));
    }

    @Test
    public void testPassedThrough() {
        Iterable<Square> passedThrough = WHITE_QUEEN_CASTLE.passedThrough();

        Collection<Square> expected = new ArrayList<Square>();
        expected.add(Square.algebraic("b1"));
        expected.add(Square.algebraic("c1"));
        expected.add(Square.algebraic("d1"));

        TestUtil.assertSameElements(expected, passedThrough);
    }

    @Test
    public void testWhiteKingCastleResult() {
        final ChessPosition before = CASTLING_POSITION_WHITE;

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        final String[] placements = {"WRa1", "WRf1", "WKg1", "BRa8", "BKe8", "BRh8"};
        expectedBuilder.placePieces(placements);
        expectedBuilder.setToMoveColor(Piece.Color.BLACK);
        expectedBuilder.updateCastlingInfo(KNOCKOUT_WHITE_CASTLE);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, WHITE_KING_CASTLE.result(before));
    }

    @Test
    public void testWhiteQueenCastleResult() {
        final ChessPosition before = CASTLING_POSITION_WHITE;

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        final String[] placements = {"WKc1", "WRd1", "WRh1", "BRa8", "BKe8", "BRh8"};
        expectedBuilder.placePieces(placements);
        expectedBuilder.setToMoveColor(Piece.Color.BLACK);
        expectedBuilder.updateCastlingInfo(KNOCKOUT_WHITE_CASTLE);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, WHITE_QUEEN_CASTLE.result(before));
    }

    @Test
    public void testBlackKingCastleResult() {
        final ChessPosition before = CASTLING_POSITION_BLACK;

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        final String[] placements = {"WRa1", "WKe1", "WRh1", "BRa8", "BRf8", "BKg8"};
        expectedBuilder.placePieces(placements);
        expectedBuilder.setToMoveColor(Piece.Color.WHITE);
        expectedBuilder.updateCastlingInfo(KNOCKOUT_BLACK_CASTLE);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, BLACK_KING_CASTLE.result(before));
    }

    @Test
    public void testBlackQueenCastleResult() {
        final ChessPosition before = CASTLING_POSITION_BLACK;

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        final String[] placements = {"WRa1", "WKe1", "WRh1", "BKc8", "BRd8", "BRh8"};
        expectedBuilder.placePieces(placements);
        expectedBuilder.setToMoveColor(Piece.Color.WHITE);
        expectedBuilder.updateCastlingInfo(KNOCKOUT_BLACK_CASTLE);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, BLACK_QUEEN_CASTLE.result(before));
    }
}
