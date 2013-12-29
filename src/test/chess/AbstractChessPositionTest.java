package test.chess;

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Tests for the chess.AbstractChessPosition class.
 * Technically, we're testing ChessPositionBuilder's concrete
 * implementation of AbstractChessPosition.  But, that
 * implementation-specific code is tested in ChessPositionBuilderTest.
 * So, if the tests in this class are failing, it's probably due
 * to a bug in AbstractChessPosition.
 */
public class AbstractChessPositionTest {
    @Test
    public void testMoves() {
        // Test new game.

        // Test king checked

        // Test castling

        // Test promotion

        // Checkmate

        // Stalemate

        fail();
    }

    @Test
    public void testOutcome() {
        // Checkmate

        // Stalemate

        fail();
    }

    @Test
    public void testShouldMax() {
        // White to move

        // Black to move

        fail();
    }

    @Test
    public void testChecked() {
        // To-move king checked.

        // Just-moved king checked.

        fail();
    }
}
