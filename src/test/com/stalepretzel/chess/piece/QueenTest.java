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
import com.stalepretzel.chess.piece.Queen;

public class QueenTest {
    private ChessPositionBuilder builder;
    private ChessPosition position;

    private static Queen WHITE_QUEEN = new Queen(Piece.Color.WHITE);

    @Before
    public void setup() {
        builder = new ChessPositionBuilder();
        builder.placePiece(new King(Piece.Color.WHITE), Square.algebraic("b2"));
        builder.placePiece(new King(Piece.Color.BLACK), Square.algebraic("b7"));
    }

    @Test
    public void testNonCaptureIsSane() {
        builder.placePiece("WQf5");
        position = builder.build();

        // Test four (of eight) movement directions.
        // The remaining four direction don't add enough value to be
        // worth the extra code!
        final NormalChessMove oneSouthEast = new NormalChessMove("f5", "g4");
        assertTrue(WHITE_QUEEN.isSane(oneSouthEast, position));
        final NormalChessMove twoEast = new NormalChessMove("f5", "h5");
        assertTrue(WHITE_QUEEN.isSane(twoEast, position));
        final NormalChessMove threeNorth = new NormalChessMove("f5", "f8");
        assertTrue(WHITE_QUEEN.isSane(threeNorth, position));
        final NormalChessMove fourSouthWest = new NormalChessMove("f5", "b1");
        assertTrue(WHITE_QUEEN.isSane(fourSouthWest, position));
    }

    @Test
    public void testCaptureIsSane() {
        builder.placePiece("WQf5");
        builder.placePiece("BRd3");
        position = builder.build();

        final NormalChessMove capture = new NormalChessMove("f5", "d3");
        assertTrue(WHITE_QUEEN.isSane(capture, position));
    }

    @Test
    public void testFriendlyCaptureIsInsane() {
        builder.placePiece("WQf5");
        builder.placePiece("WRd3");
        position = builder.build();

        final NormalChessMove capture = new NormalChessMove("f5", "d3");
        assertFalse(WHITE_QUEEN.isSane(capture, position));
    }

    @Test
    public void testHopFriendlyPieceIsInsane() {
        builder.placePiece("WQf5");
        builder.placePiece("WNd5");
        position = builder.build();

        final NormalChessMove hop = new NormalChessMove("f5", "b5");
        assertFalse(WHITE_QUEEN.isSane(hop, position));
    }

    @Test
    public void testHopEnemyPieceIsInsane() {
        builder.placePiece("WQf5");
        builder.placePiece("BNd5");
        position = builder.build();

        final NormalChessMove hop = new NormalChessMove("f5", "b5");
        assertFalse(WHITE_QUEEN.isSane(hop, position));
    }

    @Test
    public void testNonQueenMoveIsInsane() {
        builder.placePiece("WQf5");
        position = builder.build();

        final NormalChessMove knightMove = new NormalChessMove("f5", "d4");
        assertFalse(WHITE_QUEEN.isSane(knightMove, position));
    }

    @Test
    public void testSaneMoves() {
        final String[] placements = {"WQf5", "BNf7", "BNd7", "WNb5", "WNd3", "WNf2"};
        builder.placePieces(placements);
        position = builder.build();

        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        // North, until captures black knight.
        expected.add(new NormalChessMove("f5", "f6"));
        expected.add(new NormalChessMove("f5", "f7"));
        // Northwest, until captures black knight.
        expected.add(new NormalChessMove("f5", "e6"));
        expected.add(new NormalChessMove("f5", "d7"));
        // West, until hits white knight.
        expected.add(new NormalChessMove("f5", "e5"));
        expected.add(new NormalChessMove("f5", "d5"));
        expected.add(new NormalChessMove("f5", "c5"));
        // Southwest, until hits white knight.
        expected.add(new NormalChessMove("f5", "e4"));
        // South, until hits white knight.
        expected.add(new NormalChessMove("f5", "f4"));
        expected.add(new NormalChessMove("f5", "f3"));
        // Southeast, until hits boundary.
        expected.add(new NormalChessMove("f5", "g4"));
        expected.add(new NormalChessMove("f5", "h3"));
        // East, until hits boundary.
        expected.add(new NormalChessMove("f5", "g5"));
        expected.add(new NormalChessMove("f5", "h5"));
        // Northeast, until hits boundary.
        expected.add(new NormalChessMove("f5", "g6"));
        expected.add(new NormalChessMove("f5", "h7"));

        final Iterable<ChessMove> sane = WHITE_QUEEN.saneMoves(Square.algebraic("f5"), position);
        TestUtil.assertSameElements(expected, sane);
    }
}
