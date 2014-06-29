package test.java.chess.chessmove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import main.java.chess.ChessPosition;
import main.java.chess.ChessPositionBuilder;
import main.java.chess.Delta;
import main.java.chess.NormalChessMove;
import main.java.chess.Square;
import main.java.chess.piece.Piece;

import org.junit.Test;

public class NormalChessMoveTest {

    @Test
    public void testEquality() {
        final NormalChessMove e2g4 = new NormalChessMove("e2", "g4");
        final NormalChessMove e2g4Again = new NormalChessMove("e2", "g4");

        assertEquals(e2g4, e2g4Again);
        assertEquals(e2g4.hashCode(), e2g4Again.hashCode());

        final NormalChessMove e2e4 = new NormalChessMove("e2", "e4");
        assertFalse(e2e4.equals(e2g4));

        final NormalChessMove e2g2 = new NormalChessMove("e2", "g2");
        assertFalse(e2g2.equals(e2g4));
    }

    @Test
    public void testConstructors() {
        final Square start = Square.algebraic("e2");
        final Square end = Square.algebraic("e4");
        final NormalChessMove fromSquares = new NormalChessMove(start, end);

        final NormalChessMove fromStrings = new NormalChessMove("e2", "e4");

        final Delta delta = new Delta(0, 2);
        final NormalChessMove fromSquareAndDelta = new NormalChessMove(start, delta);

        final NormalChessMove fromNormalChessMove = new NormalChessMove(fromSquares);

        assertEquals(fromSquares, fromStrings);
        assertEquals(fromSquares, fromSquareAndDelta);
        assertEquals(fromSquares, fromNormalChessMove);
    }

    @Test
    public void testGetters() {
        final Square start = Square.algebraic("e2");
        final Square end = Square.algebraic("e4");
        final NormalChessMove move = new NormalChessMove(start, end);
        assertEquals(start, move.getStart());
        assertEquals(end, move.getEnd());
    }

    @Test
    public void testCapturedSquareNonCapture() {
        final ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();
        final NormalChessMove nonCaptureMove = new NormalChessMove("e2", "e4");
        assertNull(nonCaptureMove.capturedSquare(newGame));
    }

    @Test
    public void testCapturedSquareNormalCapture() {
        final String[] placements = {"WKb2", "BKb7", "WRe4", "BNe6"};
        final ChessPosition position = new ChessPositionBuilder().placePieces(placements).build();
        final NormalChessMove capture = new NormalChessMove("e4", "e6");

        final Square capturedSquare = Square.algebraic("e6");
        assertEquals(capturedSquare, capture.capturedSquare(position));
    }

    @Test
    public void testCapturedSquareEnPassant() {
        final String[] placements = {"WKb2", "BKb7", "WPe5", "BPd5"};
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePieces(placements);
        builder.setEnPassantSquare(Square.algebraic("d6"));
        final ChessPosition position = builder.build();
        final NormalChessMove enPassantCapture = new NormalChessMove("e5", "d6");

        final Square capturedSquare = Square.algebraic("d6");
        assertEquals(capturedSquare, enPassantCapture.capturedSquare(position));
    }

    @Test
    public void testEnPassantSquareDoubleStepFromHome() {
        final ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();
        final NormalChessMove enPassantCreation = new NormalChessMove("e2", "e4");
        final Square newEnPassantSquare = Square.algebraic("e3");
        assertEquals(newEnPassantSquare, enPassantCreation.enPassantSquare(newGame));
    }

    @Test
    public void testEnPassantSquareSingleStepFromHome() {
        final ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();
        final NormalChessMove singleStep = new NormalChessMove("e2", "e3");
        assertNull(singleStep.enPassantSquare(newGame));
    }

    @Test
    public void testEnPassantSquareSingleStepToFourthRank() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WKb2", "BKb7", "WPe3"};
        builder.placePieces(placements);
        final ChessPosition position = builder.build();

        final NormalChessMove singleStep = new NormalChessMove("e3", "e4");
        assertNull(singleStep.enPassantSquare(position));
    }

    @Test
    public void testEnPassantSquareNormalPush() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WKb2", "BKb7", "WPe4"};
        builder.placePieces(placements);
        final ChessPosition position = builder.build();

        final NormalChessMove normalPush = new NormalChessMove("e4", "e5");
        assertNull(normalPush.enPassantSquare(position));
    }

    @Test
    public void testEnPassantNonPawn() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WKb2", "BKb7", "WRe2"};
        builder.placePieces(placements);
        final ChessPosition position = builder.build();

        // If there were a pawn on e2, then this move would create an
        // en passant square.  But, since there's a rook on e2, it should not
        // create an en passant square.
        final NormalChessMove nonPawnPush= new NormalChessMove("e2", "e4");
        assertNull(nonPawnPush.enPassantSquare(position));
    }

    @Test
    public void testEnPassantSquareBlack() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        final String[] placements = {"WKb2", "BKb7", "BPe7"};
        builder.placePieces(placements);
        builder.setToMoveColor(Piece.Color.BLACK);
        final ChessPosition position = builder.build();

        final NormalChessMove doubleStep = new NormalChessMove("e7", "e5");
        final Square enPassantSquare = Square.algebraic("e6");
        assertEquals(enPassantSquare, doubleStep.enPassantSquare(position));
    }

    @Test
    public void testIsSaneNullPiece() {
        final ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();
        final NormalChessMove noMovingPiece = new NormalChessMove("e4", "e5");
        assertFalse(noMovingPiece.isSane(newGame));
    }

    @Test
    public void testIsSaneDelegation() {
        // We do a ton of isSane tests in test.chess.piece.
        // Here, we just check that NormalChessMove properly
        // delegates.
        final ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();

        // Test true result.
        final NormalChessMove sane = new NormalChessMove("e2", "e4");
        assertTrue(sane.isSane(newGame));

        // Test false result.
        final NormalChessMove insane = new NormalChessMove("e2", "e5");
        assertFalse(insane.isSane(newGame));
    }

    @Test
    public void testIsLegalLegalMove() {
        final ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();

        final NormalChessMove legal = new NormalChessMove("e2" ,"e4");
        assertTrue(legal.isLegal(newGame));
    }

    @Test
    public void testIsLegalKingAlreadyChecked() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePiece("WKb2").placePiece("BKb7");
        // Check the white king.
        builder.placePiece("BRb4");
        builder.placePiece("WPe2");
        final ChessPosition kingChecked = builder.build();
        final NormalChessMove ignoresCheck = new NormalChessMove("e2", "e4");
        assertFalse(ignoresCheck.isLegal(kingChecked));
    }

    @Test
    public void testIsLegalPinnedPieceMoved() {
        final ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePiece("WKb2").placePiece("BKb7");
        builder.placePiece("WPe2");
        // Pin the white pawn.
        builder.placePiece("BRg2");
        final ChessPosition pawnPinned = builder.build();
        final NormalChessMove ignoresPin = new NormalChessMove("e2", "e4");
        assertFalse(ignoresPin.isLegal(pawnPinned));
    }

    @Test
    public void testPassedThroughBasic() {
        NormalChessMove basicMove = new NormalChessMove("b2", "g2");

        Collection<Square> expected = new ArrayList<Square>();
        expected.add(Square.algebraic("c2"));
        expected.add(Square.algebraic("d2"));
        expected.add(Square.algebraic("e2"));
        expected.add(Square.algebraic("f2"));

        assertEquals(expected, basicMove.passedThrough());
    }

    @Test
    public void testPassedThroughDiagonal() {
        NormalChessMove diagonalMove = new NormalChessMove("a1", "e5");

        Collection<Square> expected = new ArrayList<Square>();
        expected.add(Square.algebraic("b2"));
        expected.add(Square.algebraic("c3"));
        expected.add(Square.algebraic("d4"));

        assertEquals(expected, diagonalMove.passedThrough());
    }

    @Test
    public void testResultNormalMove() {
        final ChessPositionBuilder beforeBuilder = new ChessPositionBuilder();
        beforeBuilder.placePiece("WKb2").placePiece("BKb7").placePiece("WPe2");
        final ChessPosition before = beforeBuilder.build();
        final NormalChessMove pawnPush = new NormalChessMove("e2", "e3");
        final ChessPosition after = pawnPush.result(before);

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        expectedBuilder.placePiece("WKb2").placePiece("BKb7").placePiece("WPe3");
        expectedBuilder.setToMoveColor(Piece.Color.BLACK);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, after);
    }

    @Test
    public void testResultCapture() {
        final ChessPositionBuilder beforeBuilder = new ChessPositionBuilder();
        beforeBuilder.placePiece("WKb2").placePiece("BKb7");
        beforeBuilder.placePiece("WRe4").placePiece("BNh4");
        final ChessPosition before = beforeBuilder.build();
        final NormalChessMove capture = new NormalChessMove("e4", "h4");
        final ChessPosition after = capture.result(before);

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        expectedBuilder.placePiece("WKb2").placePiece("BKb7").placePiece("WRh4");
        expectedBuilder.setToMoveColor(Piece.Color.BLACK);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, after);
    }

    @Test
    public void testResultEnPassantSquareAffected() {
        final ChessPositionBuilder beforeBuilder = new ChessPositionBuilder();
        beforeBuilder.placePiece("WKb2").placePiece("BKb7").placePiece("WPe2");
        final ChessPosition before = beforeBuilder.build();
        final NormalChessMove doubleStep = new NormalChessMove("e2", "e4");
        final ChessPosition after = doubleStep.result(before);

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        expectedBuilder.placePiece("WKb2").placePiece("BKb7").placePiece("WPe4");
        expectedBuilder.setEnPassantSquare(Square.algebraic("e3"));
        expectedBuilder.setToMoveColor(Piece.Color.BLACK);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, after);
    }

    @Test
    public void testResultCastlingAffected() {
        final ChessPositionBuilder beforeBuilder = new ChessPositionBuilder();
        beforeBuilder.placePiece("WKe1").placePiece("BKe8");
        beforeBuilder.placePiece("WRa1").placePiece("WRh1");
        beforeBuilder.placePiece("BRa8").placePiece("BRh8");
        final ChessPosition before = beforeBuilder.build();
        final NormalChessMove rookLift = new NormalChessMove("h1", "h2");
        final ChessPosition after = rookLift.result(before);

        final ChessPositionBuilder expectedBuilder = new ChessPositionBuilder();
        expectedBuilder.placePiece("WKe1").placePiece("BKe8");
        expectedBuilder.placePiece("WRa1").placePiece("WRh2");
        expectedBuilder.placePiece("BRa8").placePiece("BRh8");
        expectedBuilder.updateCastlingInfo(rookLift);
        expectedBuilder.setToMoveColor(Piece.Color.BLACK);
        final ChessPosition expected = expectedBuilder.build();

        assertEquals(expected, after);
    }
}
