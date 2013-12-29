package test.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import chess.CastlingInfo;
import chess.NormalChessMove;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.Game;
import chess.Piece;
import chess.Square;
import chess.exceptions.AlgebraicNotationException;
import chess.exceptions.IllegalMoveException;

public class ChessPositionBuilderTest {
    @Test
    public void testEmptyConstructor() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        ChessPosition position = builder.build();
        for (Square square : Square.ALL) {
            assertNull(position.getPiece(square));
        }
        assertNull(position.getEnPassantSquare());
        assertEquals(Piece.Color.WHITE, position.getToMoveColor());
        assertEquals(CastlingInfo.allowAll(), position.getCastlingInfo());
    }

    /** Assert that a ChessPosition is in the new-game position. */
    private void assertIsNewGame(ChessPosition position) {
        // Do a couple perfunctory spot checks for pieces.
        Piece whiteBishop = new Piece(Piece.Type.BISHOP, Piece.Color.WHITE);
        Piece c1Piece = position.getPiece(Square.algebraic("c1"));
        assertEquals(whiteBishop, c1Piece);

        Piece blackPawn = new Piece(Piece.Type.PAWN, Piece.Color.BLACK);
        Piece f7Piece = position.getPiece(Square.algebraic("f7"));
        assertEquals(blackPawn, f7Piece);

        // Make sure that all the middle squares are empty.
        for (int file = 1; file <= 8; file++) {
            for (int rank = 3; rank <= 6; rank++) {
                assertNull(position.getPiece(Square.squareAt(file, rank)));
            }
        }

        // Assert other info is correct.
        assertNull(position.getEnPassantSquare());
        assertEquals(Piece.Color.WHITE, position.getToMoveColor());
        assertEquals(CastlingInfo.allowAll(), position.getCastlingInfo());
    }

    @Test
    public void testNewGame() {
        // From blank board.
        ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.setupNewGame();
        ChessPosition newGame = builder.build();
        assertIsNewGame(newGame);

        // From non-blank board.
        ChessPositionBuilder nonBlankBuilder = new ChessPositionBuilder();

        // Place two "transient" pieces on it.
        Piece transientRook = new Piece(Piece.Type.ROOK, Piece.Color.WHITE);
        Square toBeEmpty = Square.algebraic("d5");
        // We use f7 because assertIsNewGame will check that square.
        Square toBeOccupied = Square.algebraic("f7");
        nonBlankBuilder.placePiece(transientRook, toBeEmpty);
        nonBlankBuilder.placePiece(transientRook, toBeOccupied);

        // Call setupNewGame on this "polluted" ChessPositionBuilder.
        nonBlankBuilder.setupNewGame();

        // Assert that the resulting position is still good.
       ChessPosition newGameFromNonBlankBuilder = nonBlankBuilder.build();
       assertIsNewGame(newGameFromNonBlankBuilder);
    }

    @Test
    public void testFromPositionConstructor()
            throws AlgebraicNotationException, IllegalMoveException {
        // Build an original position.
        String[] moves = {"e4", "e5", "Bc4"};
        ChessPosition originalPosition = Game.fromMoves(moves).getCurrentPosition();

        // Make a ChessPositionBuidler from the original position.
        ChessPositionBuilder builder = new ChessPositionBuilder(originalPosition);
        ChessPosition resultingPosition = builder.build();

        assertEquals(originalPosition, resultingPosition);
    }

    @Test
    public void testOnlyOneBuild() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.build();
        try {
            builder.build();
            fail();
        } catch (AssertionError expected) {
        }
    }

    @Test
    public void testPlacePiece() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        // On empty square.
        Square emptyTarget = Square.algebraic("a1");
        Piece whiteRook = new Piece(Piece.Type.ROOK, Piece.Color.WHITE);
        builder.placePiece(whiteRook, emptyTarget);

        // On non-empty square (replacement).
        Square replacementTarget = Square.algebraic("b2");
        Piece replacedPiece = new Piece(Piece.Type.BISHOP, Piece.Color.WHITE);
        Piece replacingPiece = new Piece(Piece.Type.PAWN, Piece.Color.BLACK);
        builder.placePiece(replacedPiece, replacementTarget);
        builder.placePiece(replacingPiece, replacementTarget);

        // Null piece.
        Square removalTarget = Square.algebraic("c3");
        Piece removedPiece = new Piece(Piece.Type.QUEEN, Piece.Color.BLACK);
        builder.placePiece(removedPiece, removalTarget);
        builder.placePiece(null, removalTarget);

        // Build it...
        ChessPosition position = builder.build();

        // ... and make sure everything's where it should be.
        assertEquals(whiteRook, position.getPiece(emptyTarget));
        assertEquals(replacingPiece, position.getPiece(replacementTarget));
        assertNull(position.getPiece(removalTarget));

        // Assert that we cannot call placePiece on a built ChessPositionBuilder.
        try {
            builder.placePiece(whiteRook, emptyTarget);
            fail();
        } catch (AssertionError expected) {
        }
    }

    @Test
    public void testSetEnPassantSquare() {
        Square someSquare = Square.algebraic("d5");

        // Non-null Square.
        ChessPositionBuilder nonNullEPBuilder = new ChessPositionBuilder();
        nonNullEPBuilder.setEnPassantSquare(someSquare);
        assertEquals(someSquare, nonNullEPBuilder.build().getEnPassantSquare());

        // Null Square.
        ChessPositionBuilder nullEPBuilder = new ChessPositionBuilder();
        // Set the en passant square to something...
        nullEPBuilder.setEnPassantSquare(someSquare);
        // ... then set it to null.
        nullEPBuilder.setEnPassantSquare(null);
        assertNull(nullEPBuilder.build().getEnPassantSquare());

        // Assert that we cannot call setEnPassantSquare on a built ChessPositionBuilder.
        try {
            nullEPBuilder.setEnPassantSquare(someSquare);
            fail();
        } catch (AssertionError expected) {
        }
    }

    @Test
    public void testSetToMoveColor() {
        // Set white.
        ChessPositionBuilder setWhiteBuilder = new ChessPositionBuilder();
        setWhiteBuilder.setToMoveColor(Piece.Color.WHITE);
        assertEquals(Piece.Color.WHITE, setWhiteBuilder.build().getToMoveColor());

        // Set black.
        ChessPositionBuilder setBlackBuilder = new ChessPositionBuilder();
        setBlackBuilder.setToMoveColor(Piece.Color.BLACK);
        assertEquals(Piece.Color.BLACK, setBlackBuilder.build().getToMoveColor());

        // Assert that we cannot call setToMoveColor on a built ChessPositionBuilder.
        try {
            setBlackBuilder.setToMoveColor(Piece.Color.WHITE);
            fail();
        } catch (AssertionError expected) {
        }
    }

    @Test
    public void testFlipToMoveColor() {
        // Switch White to Black.
        ChessPositionBuilder flipToWhiteBuilder = new ChessPositionBuilder();
        flipToWhiteBuilder.setToMoveColor(Piece.Color.BLACK);
        flipToWhiteBuilder.flipToMoveColor();
        assertEquals(Piece.Color.WHITE, flipToWhiteBuilder.build().getToMoveColor());

        // Switch Black to White.
        ChessPositionBuilder flipToBlackBuilder = new ChessPositionBuilder();
        flipToBlackBuilder.setToMoveColor(Piece.Color.WHITE);
        flipToBlackBuilder.flipToMoveColor();
        assertEquals(Piece.Color.BLACK, flipToBlackBuilder.build().getToMoveColor());

        // Assert that we cannot call flipToMoveColor on a built ChessPositionBuilder.
        try {
            flipToBlackBuilder.flipToMoveColor();
            fail();
        } catch (AssertionError expected) {
        }
    }

    @Test
    public void testSetCastlingInfo() {
        CastlingInfo source = CastlingInfo.allowAll();
        ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.setCastlingInfo(source);
        assertEquals(source, builder.build().getCastlingInfo());

        // Assert that we cannot call setCastlingInfo on a built ChessPositionBuilder.
        try {
            builder.setCastlingInfo(source);
            fail();
        } catch (AssertionError expected) {
        }
    }

    @Test
    public void testUpdateCastlingInfo() {
        CastlingInfo source = CastlingInfo.allowAll();
        NormalChessMove fromH1 = new NormalChessMove("h1", "h2");
        CastlingInfo whiteCannotKingCastle = source.updated(fromH1);

        ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.setCastlingInfo(source);
        builder.updateCastlingInfo(fromH1);
        assertEquals(whiteCannotKingCastle, builder.build().getCastlingInfo());

        // Assert that we cannot call updateCastlingInfo on a built ChessPositionBuilder.
        try {
            builder.updateCastlingInfo(fromH1);
            fail();
        } catch (AssertionError expected) {
        }
    }

    @Test
    public void testChessPositionImpl() {
        ChessPositionBuilder newGameBuilder = new ChessPositionBuilder();
        newGameBuilder.setupNewGame();
        ChessPosition newGame = newGameBuilder.build();

        Piece whiteBishop = new Piece(Piece.Type.BISHOP, Piece.Color.WHITE);
        Square c1 = Square.algebraic("c1");
        assertEquals(whiteBishop, newGame.getPiece(c1));
        assertNull(newGame.getEnPassantSquare());
        assertEquals(Piece.Color.WHITE, newGame.getToMoveColor());
        assertEquals(CastlingInfo.allowAll(), newGame.getCastlingInfo());

        ChessPositionBuilder differentBuilder = new ChessPositionBuilder();
        differentBuilder.setupNewGame();
        differentBuilder.setEnPassantSquare(Square.algebraic("d6"));
        differentBuilder.setToMoveColor(Piece.Color.BLACK);
        NormalChessMove fromH1 = new NormalChessMove("h1", "h2");
        CastlingInfo whiteCannotKingCastle = CastlingInfo.allowAll().updated(fromH1);
        differentBuilder.setCastlingInfo(whiteCannotKingCastle);
        ChessPosition differentPosition = differentBuilder.build();

        assertEquals(Square.algebraic("d6"), differentPosition.getEnPassantSquare());
        assertEquals(Piece.Color.BLACK, differentPosition.getToMoveColor());
        assertEquals(whiteCannotKingCastle, differentPosition.getCastlingInfo());
    }

    @Test
    public void testCompactPlacePiece() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        builder.placePiece("WPa1").placePiece("WNb2").placePiece("WBc3");
        builder.placePiece("BRd4").placePiece("BQe5").placePiece("BKf6");

        ChessPosition position = builder.build();

        Piece whitePawn = new Piece(Piece.Type.PAWN, Piece.Color.WHITE);
        Piece whiteKnight = new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE);
        Piece whiteBishop = new Piece(Piece.Type.BISHOP, Piece.Color.WHITE);
        Piece blackRook = new Piece(Piece.Type.ROOK, Piece.Color.BLACK);
        Piece blackQueen = new Piece(Piece.Type.QUEEN, Piece.Color.BLACK);
        Piece blackKing = new Piece(Piece.Type.KING, Piece.Color.BLACK);

        assertEquals(whitePawn, position.getPiece(Square.algebraic("a1")));
        assertEquals(whiteKnight, position.getPiece(Square.algebraic("b2")));
        assertEquals(whiteBishop, position.getPiece(Square.algebraic("c3")));
        assertEquals(blackRook, position.getPiece(Square.algebraic("d4")));
        assertEquals(blackQueen, position.getPiece(Square.algebraic("e5")));
        assertEquals(blackKing, position.getPiece(Square.algebraic("f6")));
    }

    @Test
    public void testCompactPlacePieces() {
        ChessPositionBuilder builder = new ChessPositionBuilder();
        String [] placements = {"WKa1", "BKh8"};
        builder.placePieces(placements);
        ChessPosition position = builder.build();

        Piece whiteKing = new Piece(Piece.Type.KING, Piece.Color.WHITE);
        Piece blackKing = new Piece(Piece.Type.KING, Piece.Color.BLACK);

        assertEquals(whiteKing, position.getPiece(Square.algebraic("a1")));
        assertEquals(blackKing, position.getPiece(Square.algebraic("h8")));
    }
}
