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
import com.stalepretzel.chess.Square;
import com.stalepretzel.chess.piece.King;
import com.stalepretzel.chess.piece.Piece;
import com.stalepretzel.chess.piece.Rook;

public class RookTest {

    private ChessPositionBuilder builder;
    private ChessPosition position;

    private static Rook WHITE_ROOK = new Rook(Piece.Color.WHITE);

    @Before
    public void setup() {
        builder = new ChessPositionBuilder();
        builder.placePiece(new King(Piece.Color.WHITE), Square.algebraic("b2"));
        builder.placePiece(new King(Piece.Color.BLACK), Square.algebraic("b7"));
    }

    @Test
    public void testMoveNorthIsSane() {
        builder.placePiece("WRe5");
        position = builder.build();
        NormalChessMove oneNorth = new NormalChessMove("e5", "e6");
        assertTrue(WHITE_ROOK.isSane(oneNorth, position));
    }

    @Test
    public void testMoveEastIsSane() {
        builder.placePiece("WRe5");
        position = builder.build();
        NormalChessMove twoEast = new NormalChessMove("e5", "c5");
        assertTrue(WHITE_ROOK.isSane(twoEast, position));
    }

    @Test
    public void testMoveSouthIsSane() {
        builder.placePiece("WRe5");
        position = builder.build();
        NormalChessMove threeSouth = new NormalChessMove("e5", "e2");
        assertTrue(WHITE_ROOK.isSane(threeSouth, position));
    }

    @Test
    public void testMoveWestIsSane() {
        builder.placePiece("WRe5");
        position = builder.build();
        NormalChessMove fourWest = new NormalChessMove("e5", "a5");
        assertTrue(WHITE_ROOK.isSane(fourWest, position));
    }

    @Test
    public void testNonBasicIsInsane() {
        builder.placePiece("WRe5");
        position = builder.build();
        NormalChessMove nonBasic = new NormalChessMove("e5", "f6");
        assertFalse(WHITE_ROOK.isSane(nonBasic, position));
    }

    @Test
    public void testHopFriendlyPieceIsInsane() {
        builder.placePiece("WRd2");
        builder.placePiece("WNd4");
        position = builder.build();
        NormalChessMove hop = new NormalChessMove("d2", "d6");
        assertFalse(WHITE_ROOK.isSane(hop, position));
    }

    @Test
    public void testHopEnemyPieceIsInsane() {
        builder.placePiece("WRd2");
        builder.placePiece("BNd4");
        position = builder.build();
        NormalChessMove hop = new NormalChessMove("d2", "d6");
        assertFalse(WHITE_ROOK.isSane(hop, position));
    }

    @Test
    public void testCaptureIsSane() {
        builder.placePiece("WRd2");
        builder.placePiece("BNd4");
        position = builder.build();
        NormalChessMove hop = new NormalChessMove("d2", "d4");
        assertTrue(WHITE_ROOK.isSane(hop, position));
    }

    @Test
    public void testFriendlyCaptureIsInsane() {
        builder.placePiece("WRd2");
        builder.placePiece("WNd4");
        position = builder.build();
        NormalChessMove hop = new NormalChessMove("d2", "d4");
        assertFalse(WHITE_ROOK.isSane(hop, position));
    }

    @Test
    public void testSaneMoves() {
        // Moving white rook.
        builder.placePiece("WRe4");
        // White knight, blocks rook after three squares.
        builder.placePiece("WNa4");
        // White knight, blocks rook after zero squares.
        builder.placePiece("WNf4");
        // Black knight, blocks rook after one square, and is capturable.
        builder.placePiece("BNe6");
        position = builder.build();

        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        // Move up to three Squares west (until hits white knight on a4).
        expected.add(new NormalChessMove("e4", "d4"));
        expected.add(new NormalChessMove("e4", "c4"));
        expected.add(new NormalChessMove("e4", "b4"));
        // Move up to three Squraes south (until hits boundary).
        expected.add(new NormalChessMove("e4", "e3"));
        expected.add(new NormalChessMove("e4", "e2"));
        expected.add(new NormalChessMove("e4", "e1"));
        // Move up to two squares north (until captures black knight).
        expected.add(new NormalChessMove("e4", "e5"));
        expected.add(new NormalChessMove("e4", "e6"));

        Iterable<ChessMove> sane = WHITE_ROOK.saneMoves(Square.algebraic("e4"), position);

        TestUtil.assertSameElements(expected, sane);
    }
}
