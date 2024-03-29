package com.stalepretzel.chess.abstractchessposition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.stalepretzel.chess.CastlingInfo;
import com.stalepretzel.chess.ChessMove;
import com.stalepretzel.chess.ChessPosition;
import com.stalepretzel.chess.ChessPositionBuilder;
import com.stalepretzel.chess.NormalChessMove;
import com.stalepretzel.chess.Square;
import com.stalepretzel.chess.piece.Piece;
import com.stalepretzel.player.Outcome;
import com.stalepretzel.player.Player;

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
    public void testToMove() {
        // White to move -- should maximize.
        ChessPositionBuilder whiteToMoveBuilder = new ChessPositionBuilder();
        whiteToMoveBuilder.setToMoveColor(Piece.Color.WHITE);
        ChessPosition whiteToMove = whiteToMoveBuilder.build();
        assertEquals(Player.MAXIMIZER, whiteToMove.toMove());

        // Black to move -- should not maximize.
        ChessPositionBuilder blackToMoveBuilder = new ChessPositionBuilder();
        blackToMoveBuilder.setToMoveColor(Piece.Color.BLACK);
        ChessPosition blackToMove = blackToMoveBuilder.build();
        assertEquals(Player.MINIMIZER, blackToMove.toMove());
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

    @Test
    public void testPromotionChecked() {
        String[] whiteCheckedByPawnPlacements = {"WKe1", "BKe8", "BPf2"};
        ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePieces(whiteCheckedByPawnPlacements);
        ChessPosition whiteCheckedByPawn = builder.build();
        assertTrue(whiteCheckedByPawn.checked(Piece.Color.WHITE));
    }

    @Test
    public void testEquality() {
        // Assert that two equal (new) ChessPositions are equal, and have the same hashCode.
        ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();
        ChessPosition newGameAgain = new ChessPositionBuilder().setupNewGame().build();
        assertEquals(newGame, newGameAgain);
        assertEquals(newGame.hashCode(), newGameAgain.hashCode());

        // Assert that differing pieces make ChessPositions unequal.
        ChessPosition noQueenRook = new ChessPositionBuilder().setupNewGame()
                                    .vacate(Square.algebraic("a1")).build();
        assertFalse(noQueenRook.equals(newGame));

        // Assert that differing toMoveColors make ChessPositions unequal.
        ChessPosition blackToMove = new ChessPositionBuilder().setupNewGame()
                                    .setToMoveColor(Piece.Color.BLACK).build();
        assertFalse(blackToMove.equals(newGame));

        // Assert that differing enPassantSquares make ChessPositions unequal.
        ChessPosition enPassantPossible = new ChessPositionBuilder().setupNewGame()
                                          .setEnPassantSquare(Square.algebraic("d5"))
                                          .build();
        assertFalse(enPassantPossible.equals(newGame));

        // Assert that differing castlingInfos make ChessPositions unequal.
        ChessMove fromH1 = new NormalChessMove("h1", "h2");
        CastlingInfo whiteCannotKingCastle = CastlingInfo.allowAll().updated(fromH1);
        ChessPosition castlingInfoDifferent = new ChessPositionBuilder().setupNewGame()
                                              .setCastlingInfo(whiteCannotKingCastle).build();
        assertFalse(castlingInfoDifferent.equals(newGame));
    }
}
