package test.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import test.TestUtil;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import chess.Game;
import chess.NormalChessMove;
import chess.Piece;
import chess.Square;
import chess.exceptions.AlgebraicNotationException;
import chess.exceptions.IllegalMoveException;

public class GameTest {

    @Test
    public void testConstructor() {
        Game newGame = new Game();
        TestUtil.assertIsNewGame(newGame.getCurrentPosition());
    }

    @Test
    public void testMakeMoveAndGetCurrentPosition() throws IllegalMoveException {
        Game ePawnsPushedGame = new Game().makeMove(new NormalChessMove("e2", "e4"))
                                .makeMove(new NormalChessMove("e7", "e5"));
        ChessPosition ePawnsPushed = ePawnsPushedGame.getCurrentPosition();

        // Assert that those moves worked.
        // We don't do any hard-cord testing of the resulting ChessPosition.
        // That will be done in the tests for ChessMove's implementations.
        Piece whitePawn = new Piece(Piece.Type.PAWN, Piece.Color.WHITE);
        Piece blackPawn = new Piece(Piece.Type.PAWN, Piece.Color.BLACK);
        assertEquals(whitePawn, ePawnsPushed.getPiece(Square.algebraic("e4")));
        assertNull(ePawnsPushed.getPiece(Square.algebraic("e2")));
        assertEquals(blackPawn, ePawnsPushed.getPiece(Square.algebraic("e5")));
        assertNull(ePawnsPushed.getPiece(Square.algebraic("e7")));
    }

    @Test
    public void testIllegalMoves() {
        Game illegalMoveTarget = new Game();
        ChessMove illegalMove = new NormalChessMove("a1", "h8");
        try {
            illegalMoveTarget.makeMove(illegalMove);
            fail("Successfully made an illegal move on a Game.");
        } catch (IllegalMoveException expected) {
        }
    }

    @Test
    public void testFromMoves() throws AlgebraicNotationException, IllegalMoveException {
        // Fried liver attack!
        // Used because it's somewhat varied:
        // Different pieces, different captures,
        // checks.
        String[] moves = {"e4", "e5",
                          "Nf3", "Nc6",
                          "Bc4", "Nf6",
                          "Ng5", "d5",
                          "exd5", "Nxd5",
                          "Nxf7", "Kxf7",
                          // TODO(jasonpr): Indicate Check ("Qf3+") once it's implemented!
                          "Qf3"};

        // Just ensure that this executes without error.
        Game.fromMoves(moves);
    }

    @Test
    public void testGetPosition() throws AlgebraicNotationException, IllegalMoveException {
        String[] moves = {"e4", "e5", "Nf3"};
        Game kingKnight = Game.fromMoves(moves);

        // Assert zero'th move is new-game position.
        ChessPosition newGame = new ChessPositionBuilder().setupNewGame().build();
        assertEquals(newGame, kingKnight.getPosition(0, 0));
        // ...even for a strange "ply" value.
        assertEquals(newGame, kingKnight.getPosition(0, 20));

        final Piece whitePawn = new Piece(Piece.Type.PAWN, Piece.Color.WHITE);
        final Piece blackPawn = new Piece(Piece.Type.PAWN, Piece.Color.BLACK);
        final Piece whiteKnight = new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE);

        // Assert after first full move, position is correct.
        ChessPosition open = new ChessPositionBuilder().setupNewGame()
                             .placePiece(null, Square.algebraic("e2"))
                             .placePiece(whitePawn, Square.algebraic("e4"))
                             .placePiece(null, Square.algebraic("e7"))
                             .placePiece(blackPawn, Square.algebraic("e5"))
                             .setEnPassantSquare(Square.algebraic("e6"))
                             .build();

        assertEquals(open, kingKnight.getPosition(1, 2));

        // Assert after first move and a half, position is correct.
        ChessPosition kingKnightPosition = new ChessPositionBuilder().setupNewGame()
                                           .placePiece(null, Square.algebraic("e2"))
                                           .placePiece(whitePawn, Square.algebraic("e4"))
                                           .placePiece(null, Square.algebraic("e7"))
                                           .placePiece(blackPawn, Square.algebraic("e5"))
                                           .placePiece(null, Square.algebraic("g1"))
                                           .placePiece(whiteKnight, Square.algebraic("f3"))
                                           .setToMoveColor(Piece.Color.BLACK)
                                           .build();
        assertEquals(kingKnightPosition, kingKnight.getPosition(2, 1));
    }
}
