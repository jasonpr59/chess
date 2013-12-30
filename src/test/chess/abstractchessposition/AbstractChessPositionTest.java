package test.chess.abstractchessposition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import player.Outcome;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.Piece;

/**
 * Tests for the chess.AbstractChessPosition class.
 *
 * Technically, we're testing ChessPositionBuilder's concrete
 * implementation of AbstractChessPosition.  But, that
 * implementation-specific code is tested in ChessPositionBuilderTest.
 * So, if the tests in this class are failing, it's probably due
 * to a bug in AbstractChessPosition.
 *
 * Tests for the `moves` method are in AbstractChessPositionMovesTest.
 */
public class AbstractChessPositionTest {

    @Test
    public void testOutcome() {
        // Checkmate
        ChessPositionBuilder checkmateBuilder = new ChessPositionBuilder();
        String[] checkmatePlacements = {"WKa1", "BQb2", "BKc3"};
        checkmateBuilder.placePieces(checkmatePlacements);
        ChessPosition checkmate = checkmateBuilder.build();

        assertEquals(Outcome.LOSS, checkmate.outcome());

        // Stalemate
        ChessPositionBuilder stalemateBuilder = new ChessPositionBuilder();
        String[] stalematePlacements = {"WKa1", "BKa3", "BRb8"};
        stalemateBuilder.placePieces(stalematePlacements);
        ChessPosition stalemate = stalemateBuilder.build();

        assertEquals(Outcome.DRAW, stalemate.outcome());
    }

    @Test
    public void testShouldMaximize() {
        // White to move -- should maximize.
        ChessPositionBuilder whiteToMoveBuilder = new ChessPositionBuilder();
        whiteToMoveBuilder.setToMoveColor(Piece.Color.WHITE);
        ChessPosition whiteToMove = whiteToMoveBuilder.build();
        assertTrue(whiteToMove.shouldMaximize());

        // Black to move -- should not maximize.
        ChessPositionBuilder blackToMoveBuilder = new ChessPositionBuilder();
        blackToMoveBuilder.setToMoveColor(Piece.Color.BLACK);
        ChessPosition blackToMove = blackToMoveBuilder.build();
        assertFalse(blackToMove.shouldMaximize());
    }

    @Test
    public void testChecked() {
        String[] whiteCheckedPlacements = {"WKa1", "BQa3", "BKa5"};
        // To-move king checked.  That is, it's white to move, and
        // white is in check.
        ChessPositionBuilder whiteToMoveBuilder = new ChessPositionBuilder();
        whiteToMoveBuilder.placePieces(whiteCheckedPlacements);
        ChessPosition whiteToMove = whiteToMoveBuilder.build();
        assertTrue(whiteToMove.checked(Piece.Color.WHITE));

        // Just-moved king checked.  That is, it's black to move, and
        // white is in check (this indicates an illegal position).
        ChessPositionBuilder blackToMoveBuilder = new ChessPositionBuilder();
        blackToMoveBuilder.placePieces(whiteCheckedPlacements);
        blackToMoveBuilder.setToMoveColor(Piece.Color.BLACK);
        ChessPosition blackToMove = blackToMoveBuilder.build();
        assertTrue(blackToMove.checked(Piece.Color.WHITE));
    }
}
