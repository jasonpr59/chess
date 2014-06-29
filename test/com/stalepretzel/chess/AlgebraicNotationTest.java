package com.stalepretzel.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.stalepretzel.chess.AlgebraicNotation;
import com.stalepretzel.chess.ChessMove;
import com.stalepretzel.chess.ChessPosition;
import com.stalepretzel.chess.ChessPositionBuilder;
import com.stalepretzel.chess.NormalChessMove;
import com.stalepretzel.chess.PromotionMove;
import com.stalepretzel.chess.exceptions.AlgebraicNotationException;
import com.stalepretzel.chess.piece.Bishop;
import com.stalepretzel.chess.piece.Knight;
import com.stalepretzel.chess.piece.Piece;
import com.stalepretzel.chess.piece.Queen;
import com.stalepretzel.chess.piece.Rook;

public class AlgebraicNotationTest {

    private ChessPositionBuilder builder;
    private ChessPosition position;

    @Before
    public void setup() {
        builder = new ChessPositionBuilder();
        builder.placePiece("WKb2").placePiece("BKb7");
    }


    // TODO: Determine whether to require AlgebraicNotationException
    // on over-specific clues.

    @Test
    public void testPawnMove() throws AlgebraicNotationException {
        builder.placePiece("WPe2");
        position = builder.build();
        ChessMove pawnPush = AlgebraicNotation.parse("e4", position);
        assertEquals(new NormalChessMove("e2", "e4"), pawnPush);
    }

    @Test
    public void testKnightMove() throws AlgebraicNotationException {
        builder.placePiece("WNe5");
        position = builder.build();
        ChessMove knightHop = AlgebraicNotation.parse("Ng6", position);
        assertEquals(new NormalChessMove("e5", "g6"), knightHop);
    }

    @Test
    public void testBishopMove() throws AlgebraicNotationException {
        builder.placePiece("WBe5");
        position = builder.build();
        ChessMove bishopSlide = AlgebraicNotation.parse("Bg3", position);
        assertEquals(new NormalChessMove("e5", "g3"), bishopSlide);
    }

    @Test
    public void testRookMove() throws AlgebraicNotationException {
        builder.placePiece("WRh1");
        position = builder.build();
        ChessMove rookLift = AlgebraicNotation.parse("Rh3", position);
        assertEquals(new NormalChessMove("h1", "h3"), rookLift);
    }

    @Test
    public void testQueenMove() throws AlgebraicNotationException {
        builder.placePiece("WQd1");
        position = builder.build();
        ChessMove queenUp = AlgebraicNotation.parse("Qd2", position);
        assertEquals(new NormalChessMove("d1", "d2"), queenUp);
    }

    @Test
    public void testKingMove() throws AlgebraicNotationException {
        position = builder.build();
        ChessMove kingRun = AlgebraicNotation.parse("Kc2", position);
        assertEquals(new NormalChessMove("b2", "c2"), kingRun);
    }

    @Test
    public void testNoMatchingPiece() {
        // Build the position with *no* rook.
        position = builder.build();
        try {
            AlgebraicNotation.parse("Re2", position);
            fail("Moved nonexistent rook!");
        } catch (AlgebraicNotationException expected) {
        }
    }

    @Test
    public void testMultipleMatchingPieces() {
        builder.placePiece("WRe4").placePiece("WRg4");
        position = builder.build();
        try {
            AlgebraicNotation.parse("Rf4", position);
            fail("Didn't complain that it is ambigious which rook to choose!");
        } catch (AlgebraicNotationException expected) {
        }
    }

    @Test
    public void testFileClue() throws AlgebraicNotationException {
        builder.placePiece("WRe4").placePiece("WRg4");
        position = builder.build();
        ChessMove eRookSlide = AlgebraicNotation.parse("Ref4", position);
        ChessMove gRookSlide = AlgebraicNotation.parse("Rgf4", position);
        assertEquals(new NormalChessMove("e4", "f4"), eRookSlide);
        assertEquals(new NormalChessMove("g4", "f4"), gRookSlide);
    }

    @Test
    public void testRankClue() throws AlgebraicNotationException {
        builder.placePiece("WRf3").placePiece("WRf5");
        position = builder.build();
        ChessMove rank3RookSlide = AlgebraicNotation.parse("R3f4", position);
        ChessMove rank5RookSlide = AlgebraicNotation.parse("R5f4", position);
        assertEquals(new NormalChessMove("f3", "f4"), rank3RookSlide);
        assertEquals(new NormalChessMove("f5", "f4"), rank5RookSlide);
    }

    @Test
    public void testSquareClue() throws AlgebraicNotationException {
        // Three knights, all attacking f5.
        builder.placePiece("WNe7").placePiece("WNg7").placePiece("WNg3");
        position = builder.build();
        ChessMove knightHop = AlgebraicNotation.parse("Ng7f5", position);
        assertEquals(new NormalChessMove("g7", "f5"), knightHop);
    }

    @Test
    public void testSquareClueWrongPiece() {
        // Place a Knight...
        builder.placePiece("WNe6");
        position = builder.build();
        try {
            // ... but say there's a rook there.
            AlgebraicNotation.parse("Re6e5", position);
            fail("Didn't complain that moving piece is of wrong type.");
        } catch (AlgebraicNotationException expected) {
        }
    }

    @Test
    public void testTakesCorrectlyShown() throws AlgebraicNotationException {
        builder.placePiece("WRe5");
        builder.placePiece("BNe6");
        position = builder.build();
        ChessMove takeKnight = AlgebraicNotation.parse("Rxe6", position);
        assertEquals(new NormalChessMove("e5", "e6"), takeKnight);
    }

    @Test
    public void testTakesMistakenlyShown() {
        builder.placePiece("WRe5");
        position = builder.build();
        try {
            AlgebraicNotation.parse("Rxe6", position);
            fail("Didn't complain that non-capturing move has an 'x'.");
        } catch (AlgebraicNotationException expected){
        }
    }

    @Test
    public void testTakesMistakenlyOmitted() {
        builder.placePiece("WRe5");
        builder.placePiece("BNe6");
        position = builder.build();
        try {
            AlgebraicNotation.parse("Re6", position);
            fail("Didn't complain that capturing move has no 'x'.");
        } catch (AlgebraicNotationException expected){
        }
    }

    @Test
    public void testCheckCorrectlyShown() throws AlgebraicNotationException {
        // Recall, the black king is on b7.
        builder.placePiece("WRe5");
        position = builder.build();
        ChessMove checks = AlgebraicNotation.parse("Re7+", position);
        assertEquals(new NormalChessMove("e5", "e7"), checks);
    }

    @Test
    public void testCheckMistakenlyShown() {
        builder.placePiece("WRe5");
        position = builder.build();
        try {
            AlgebraicNotation.parse("Re6+", position);
            fail("Didn't complain that non-checking move has a '+'.");
        } catch (AlgebraicNotationException expected) {
        }
    }

    @Test
    public void testCheckMistakenlyOmitted() {
        // Recall, the black king is on b7.
        builder.placePiece("WRe5");
        position = builder.build();
        try {
            AlgebraicNotation.parse("Re7", position);
            fail("Didn't complain that checking move has no '+'.");
        } catch (AlgebraicNotationException expected) {
        }
    }

    @Test
    public void testCheckmateCorrectlyShown() throws AlgebraicNotationException {
        // Recall, the black king is on b7.
        builder.placePiece("WRa5").placePiece("WRc5");
        builder.placePiece("WRe4");
        position = builder.build();
        ChessMove checkmate = AlgebraicNotation.parse("Rb4#", position);
        assertEquals(new NormalChessMove("e4", "b4"), checkmate);
    }

    @Test
    public void testCheckmateMistakenlyShown() {
        // Recall, the black king is on b7.
        builder.placePiece("WRa5");
        builder.placePiece("WRe4");
        position = builder.build();
        // Not checkmate-- black king can escape to c-file.
        try {
            AlgebraicNotation.parse("Rb4#", position);
            fail("Didn't complain that non-checkmate move has a '#'.");
        } catch (AlgebraicNotationException expected) {
        }
    }

    @Test
    public void testCheckmateMistakenlyOmitted() {
        // Recall, the black king is on b7.
        builder.placePiece("WRa5").placePiece("WRc5");
        builder.placePiece("WRe4");
        position = builder.build();
        try {
            AlgebraicNotation.parse("Ra4", position);
            fail("Didn't complain that checkmate move has no '#'.");
        } catch (AlgebraicNotationException expected) {
        }
    }


    @Test
    public void testPromotionCorrectlyShown() throws AlgebraicNotationException {
        builder.placePiece("WPe7");
        position = builder.build();

        ChessMove pushToKnight = AlgebraicNotation.parse("e8=N", position);
        ChessMove pushToBishop = AlgebraicNotation.parse("e8=B", position);
        ChessMove pushToRook = AlgebraicNotation.parse("e8=R", position);
        ChessMove pushToQueen = AlgebraicNotation.parse("e8=Q", position);

        NormalChessMove basePush = new NormalChessMove("e7", "e8");
        assertEquals(new PromotionMove(basePush, new Knight(Piece.Color.WHITE)), pushToKnight);
        assertEquals(new PromotionMove(basePush, new Bishop(Piece.Color.WHITE)), pushToBishop);
        assertEquals(new PromotionMove(basePush, new Rook(Piece.Color.WHITE)), pushToRook);
        assertEquals(new PromotionMove(basePush, new Queen(Piece.Color.WHITE)), pushToQueen);
    }

    @Test
    public void testPromotionMistakenlyShownWrongRank() {
        builder.placePiece("WPe6");
        position = builder.build();
        try {
            AlgebraicNotation.parse("e7=Q", position);
            fail("Didn't complain that a non-final-rank push has a promotion symbol.");
        } catch (AlgebraicNotationException expected) {
        }
    }

    @Test
    public void testPromotionMistakenlyShownWrongPiece() {
        builder.placePiece("WRe7");
        // Place this knight here to block the black king on b7 from check.
        // It's awkward, but it works.
        builder.placePiece("WNc7");
        position = builder.build();
        try {
            AlgebraicNotation.parse("Re8=Q", position);
            fail("Didn't complain that a non-pawn move has a promotion symbol.");
        } catch (AlgebraicNotationException expected) {
        }
    }

    @Test
    public void testPromotionMistakenlyOmitted() {
        builder.placePiece("WPe7");
        position = builder.build();
        try {
            AlgebraicNotation.parse("e8", position);
            fail("Didn't complain that a final-rank push has no promotion symbol.");
        } catch (AlgebraicNotationException expected) {
        }
    }
}
