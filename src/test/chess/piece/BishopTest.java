package test.chess.piece;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import test.TestUtil;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.NormalChessMove;
import chess.Square;
import chess.piece.Bishop;
import chess.piece.King;
import chess.piece.Piece;

public class BishopTest {
    private ChessPositionBuilder builder;
    private ChessPosition position;

    private static Bishop WHITE_BISHOP = new Bishop(Piece.Color.WHITE);

    @Before
    public void setup() {
        builder = new ChessPositionBuilder();
        builder.placePiece(new King(Piece.Color.WHITE), Square.algebraic("b2"));
        builder.placePiece(new King(Piece.Color.BLACK), Square.algebraic("b7"));
    }

    @Test
    public void testMoveNorthWestIsSane() {
        builder.placePiece("WBe5");
        position = builder.build();

        NormalChessMove northWestOne = new NormalChessMove("e5", "f4");
        assertTrue(WHITE_BISHOP.isSane(northWestOne, position));
    }

    @Test
    public void testMoveNorthEastIsSane() {
        builder.placePiece("WBe5");
        position = builder.build();

        NormalChessMove northEastTwo = new NormalChessMove("e5", "g7");
        assertTrue(WHITE_BISHOP.isSane(northEastTwo, position));
    }

    @Test
    public void testMoveSouthWestIsSane() {
        builder.placePiece("WBg6");
        position = builder.build();

        NormalChessMove southWestThree = new NormalChessMove("g6", "d3");
        assertTrue(WHITE_BISHOP.isSane(southWestThree, position));
    }

    @Test
    public void testMoveSouthEastIsSane() {
        builder.placePiece("WBc7");
        position = builder.build();

        NormalChessMove southEastFour = new NormalChessMove("c7", "g3");
        assertTrue(WHITE_BISHOP.isSane(southEastFour, position));
    }

    @Test
    public void testNonDiagonalMoveIsInsane() {
        builder.placePiece("WBc3");
        position = builder.build();

        NormalChessMove nonDiagonal = new NormalChessMove("c3", "f4");
        assertFalse(WHITE_BISHOP.isSane(nonDiagonal, position));
    }

    @Test
    public void testHopFriendlyPieceIsInsane() {
        builder.placePiece("WBc3");
        builder.placePiece("WRe5");
        position = builder.build();

        NormalChessMove hop = new NormalChessMove("c3", "g7");
        assertFalse(WHITE_BISHOP.isSane(hop, position));
    }

    @Test
    public void testHopEnemyPieceIsInsane() {
        builder.placePiece("WBc3");
        builder.placePiece("BRe5");
        position = builder.build();

        NormalChessMove hop = new NormalChessMove("c3", "g7");
        assertFalse(WHITE_BISHOP.isSane(hop, position));
    }

    @Test
    public void testCaptureIsSane() {
        builder.placePiece("WBc3");
        builder.placePiece("BRe5");
        position = builder.build();

        NormalChessMove capture = new NormalChessMove("c3", "e5");
        assertTrue(WHITE_BISHOP.isSane(capture, position));
    }

    @Test
    public void testFriendlyCaptureIsInsane() {
        builder.placePiece("WBc3");
        builder.placePiece("WRe5");
        position = builder.build();

        NormalChessMove capture = new NormalChessMove("c3", "e5");
        assertFalse(WHITE_BISHOP.isSane(capture, position));
    }

    @Test
    public void testSaneMoves() {
        builder.placePiece("WBf5");
        // Obstructing bishop, after two empty squares.
        builder.placePiece("WRc2");
        // Obstructing bishop, after zero empty squares.
        // That is, bishop cannot move in this direction at all.
        builder.placePiece("WNg4");
        // Obstructing bishop, after one square.  This is capturable.
        builder.placePiece("BNd7");
        position = builder.build();

        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        expected.add(new NormalChessMove("f5", "e6"));
        expected.add(new NormalChessMove("f5", "d7"));
        expected.add(new NormalChessMove("f5", "e4"));
        expected.add(new NormalChessMove("f5", "d3"));
        expected.add(new NormalChessMove("f5", "g6"));
        expected.add(new NormalChessMove("f5", "h7"));

        Iterable<ChessMove> sane = WHITE_BISHOP.saneMoves(Square.algebraic("f5"), position);

        TestUtil.assertSameElements(expected, sane);
    }
}
