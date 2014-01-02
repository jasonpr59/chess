package test.chess.piece;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.Square;
import chess.piece.King;
import chess.piece.Piece;
import chess.piece.Queen;

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
        fail();
    }

    @Test
    public void testCaptureIsSane() {
        fail();
    }

    @Test
    public void testFriendlyCaptureIsInsane() {
        fail();
    }

    @Test
    public void testHopFriendlyPieceIsInsane() {
        fail();
    }

    @Test
    public void testHopEnemyPieceIsInsane() {
        fail();
    }

    @Test
    public void testNonQueenMoveIsInsane() {
        fail();
    }

    @Test
    public void testSaneMoves() {
        fail();
    }
}
