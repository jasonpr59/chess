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
import com.stalepretzel.chess.piece.Knight;
import com.stalepretzel.chess.piece.Piece;

public class KnightTest {

    private ChessPositionBuilder builder;
    private ChessPosition position;

    private static Knight WHITE_KNIGHT = new Knight(Piece.Color.WHITE);

    @Before
    public void setup() {
        builder = new ChessPositionBuilder();
        builder.placePiece(new King(Piece.Color.WHITE), Square.algebraic("b2"));
        builder.placePiece(new King(Piece.Color.BLACK), Square.algebraic("b7"));
    }

    @Test
    public void testNonCaptureIsSane() {
        builder.placePiece("WNe5");
        position = builder.build();

        NormalChessMove standing = new NormalChessMove("e5", "f7");
        assertTrue(WHITE_KNIGHT.isSane(standing, position));

        NormalChessMove lyingDown = new NormalChessMove("e5", "g6");
        assertTrue(WHITE_KNIGHT.isSane(lyingDown, position));

        NormalChessMove negativeDeltaComponents = new NormalChessMove("e5", "d3");
        assertTrue(WHITE_KNIGHT.isSane(negativeDeltaComponents, position));
    }

    @Test
    public void testCaptureIsSane() {
        builder.placePiece("WNe5");
        builder.placePiece("BRf7");
        position = builder.build();

        NormalChessMove capture = new NormalChessMove("e5", "f7");
        assertTrue(WHITE_KNIGHT.isSane(capture, position));
    }

    @Test
    public void testFriendlyCaptureIsInsane() {
        builder.placePiece("WNe5");
        builder.placePiece("WRf7");
        position = builder.build();

        NormalChessMove capture = new NormalChessMove("e5", "f7");
        assertFalse(WHITE_KNIGHT.isSane(capture, position));
    }

    @Test
    public void testBadDeltaIsInsane() {
        builder.placePiece("WNe5");
        position = builder.build();

        NormalChessMove withBadDelta = new NormalChessMove("e5", "e7");
        assertFalse(WHITE_KNIGHT.isSane(withBadDelta, position));
    }

    @Test
    public void testSaneMoves() {
        builder.placePiece("WNg6");
        // Black rook which can be captured.
        builder.placePiece("BRf8");
        // White rook which blocks the white knight from moving to h4.
        builder.placePiece("WRh4");
        position = builder.build();


        Collection<ChessMove> expected = new ArrayList<ChessMove>();
        expected.add(new NormalChessMove("g6", "h8"));
        expected.add(new NormalChessMove("g6", "f8"));
        expected.add(new NormalChessMove("g6", "e7"));
        expected.add(new NormalChessMove("g6", "e5"));
        expected.add(new NormalChessMove("g6", "f4"));

        Iterable<ChessMove> sane = WHITE_KNIGHT.saneMoves(Square.algebraic("g6"), position);

        TestUtil.assertSameElements(expected, sane);
    }
}
